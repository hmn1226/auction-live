package com.auctionmachine.resources.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auctionmachine.resources.model.UserModel;
import com.auctionmachine.resources.model.VerificationModel;
import com.auctionmachine.resources.repository.UserRepository;
import com.auctionmachine.resources.repository.VerificationRepository;
import com.auctionmachine.resources.schema.ExceptionResponse;
import com.auctionmachine.resources.schema.MessageResponse;
import com.auctionmachine.resources.schema.auth.AuthLoginRequest;
import com.auctionmachine.resources.schema.auth.AuthRegisterRequest;
import com.auctionmachine.resources.schema.auth.AuthVerifyRequest;
import com.auctionmachine.util.DateUtil;
import com.auctionmachine.util.JwtUtil;
import com.auctionmachine.util.SesUtil;

import de.huxhorn.sulky.ulid.ULID;
import software.amazon.awssdk.services.ses.model.SesException;

@Service
public class AuthService {
	
	private Logger logger = LoggerFactory.getLogger(super.getClass());

	@Autowired
	private JwtUtil jwtUtil;

	@Value("${app.base-url}")
	private String baseUrl;

	@Value("${app.email-verification-path}")
	private String emailVerificationPath;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VerificationRepository verificationRepository;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public ResponseEntity<?> login(AuthLoginRequest request) {
		UserModel userModel = userRepository.getByEmail(request.getEmail());

		if (userModel == null || !passwordEncoder.matches(request.getPassword(), userModel.getPassword())) {
			return loginError();
		}
		if(userModel.getVerified()==false) {
			return loginErrorUnverified();
		}
		return loginSuccess(userModel);	
	}
	
	private ResponseEntity<?> loginSuccess(UserModel userModel) {		
		UserDetails userDetails = User
				.withUsername(userModel.getUlid())
				.password("")
				.roles(userModel.getRole())
				.build();

		String token = jwtUtil.generateToken(userDetails);
		Map<String, String> response = new HashMap<>();
		response.put("token", token);
		return ResponseEntity.ok(response);
	}

	private ResponseEntity<?> loginError() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ExceptionResponse("メールアドレスまたはパスワードが間違っています"));
	}
	private ResponseEntity<?> loginErrorUnverified() {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ExceptionResponse("認証されていないメールアドレスです"));
	}

	public ResponseEntity<?> register(AuthRegisterRequest request){
		UserModel userModel = userRepository.getByEmail(request.getEmail());
		
		if(userModel==null) {
			String verificationToken = UUID.randomUUID().toString();
			String verificationCode = generateVerificationCode();
			this.registVerificationModel(verificationToken, verificationCode, request);
			this.registUserModel(request,verificationToken, verificationCode);
			this.sendVerificationEmail(request.getEmail(), verificationToken, verificationCode);
			return ResponseEntity.ok(new MessageResponse("登録完了！メールを確認してください。"));
		}else {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new ExceptionResponse("このメールアドレスは既に登録されています"));
		}
	}

	public ResponseEntity<?> verify(AuthVerifyRequest authVerifyRequest){		
		VerificationModel verificationModel = 
				this.verificationRepository.findByVerificationToken(authVerifyRequest.getVerificationToken());

		if (verificationModel == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ExceptionResponse("認証トークンが間違っています。"));
		}
		LocalDateTime now = LocalDateTime.now();
		if (DateUtil.toLocalDateTime(verificationModel.getExpiresAt()).isBefore(now)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ExceptionResponse("認証コードの有効期限が切れています。"));
		}
		if (!verificationModel.getVerificationCode().equals(authVerifyRequest.getVerificationCode())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ExceptionResponse("認証コードが間違っています。"));
		}
		verificationModel.setVerified(true);
		verificationRepository.save(verificationModel);
		
		//ユーザーモデルのverified更新
		UserModel userModel = this.userRepository.getByEmail(verificationModel.getEmail());
		userModel.setVerified(true);
		userRepository.save(userModel);

		return ResponseEntity.ok().body(new MessageResponse("認証に成功しました。"));
	}

	private void registUserModel(AuthRegisterRequest request,String verificationToken,String verificationCode) {
		ULID ulid = new ULID();
	    String generatedUlid = ulid.nextULID();
		UserModel newUser = new UserModel();
		newUser.setEmail(request.getEmail());
		newUser.setNickname(request.getNickname());
		newUser.setPassword(passwordEncoder.encode(request.getPassword()));
		newUser.setUlid(generatedUlid);
		newUser.setRole("NORMAL");
		userRepository.save(newUser);
	}

	private void registVerificationModel(String verificationToken,String verificationCode,AuthRegisterRequest request) {
		VerificationModel verificationModel = new VerificationModel();
		verificationModel.setVerificationCode(verificationCode);
		verificationModel.setVerificationToken(verificationToken);
		verificationModel.setEmail(request.getEmail());
		verificationModel.setCreateAt(DateUtil.toString(LocalDateTime.now()));
		verificationModel.setExpiresAt(DateUtil.toString(LocalDateTime.now().plusHours(1)));
		this.verificationRepository.save(verificationModel);
	}
	
	private void sendVerificationEmail(String email, String verificationToken, String verificationCode) {
		String verificationUrl = baseUrl + emailVerificationPath + verificationToken;
		String subject = "【AuctionLIVE!】認証コードのお知らせ";
		String body = 
				"<h1>認証コードのお知らせ</h1>"+
				"こんにちは。"+email+"様 <br><br>"+
				"メールアドレスの認証コードはこちらになります。<br><br> " + 
				verificationCode + "<br><br>" +
				
				"以下のリンクをクリックしてメール認証を完了してください。<br>" +
				verificationUrl + "<br>" +
				"このコードは一定時間後に無効になります。<br>"+
				"セキュリティのため、この認証コードを第三者に共有しないでください。"+
				"認証コードを発行したことにお心当たりがない場合は、本メールを削除いただきますようお願いいたします。";
		try {
			SesUtil sesUtil = new SesUtil(Arrays.asList(email),subject,body);
			sesUtil.send();
			logger.info("認証メールを送信しました: " + email);
		} catch (SesException e) {
			logger.error("メール送信エラー: " + e.awsErrorDetails().errorMessage());
		}
	}

	private String generateVerificationCode() {
		SecureRandom random = new SecureRandom();
		int code = 100000 + random.nextInt(900000);
		return String.valueOf(code);
	}
}
