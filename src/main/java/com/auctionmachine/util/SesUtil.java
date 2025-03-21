package com.auctionmachine.util;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;

/**
 * Amazon SESを使用してメールを送信するユーティリティクラス
 */
@Component
public class SesUtil {

    private List<String> recipientEmails;
    private String subject;
    private String body;
    private SesClient sesClient;

    // 設定ファイルから読み込む値
    private static String accessKey;
    private static String secretKey;
    private static String senderEmail;
    private static String region;
    private static final String CHARSET = "UTF-8";

    /**
     * 設定値を注入する
     * 
     * @param accessKey AWS アクセスキー
     * @param secretKey AWS シークレットキー
     * @param senderEmail 送信者メールアドレス
     * @param region AWSリージョン
     */
    @Value("${aws.access-key}")
    public void setAccessKey(String accessKey) {
        SesUtil.accessKey = accessKey;
    }

    @Value("${aws.secret-key}")
    public void setSecretKey(String secretKey) {
        SesUtil.secretKey = secretKey;
    }

    @Value("${aws.ses.sender-email}")
    public void setSenderEmail(String senderEmail) {
        SesUtil.senderEmail = senderEmail;
    }

    @Value("${aws.region}")
    public void setRegion(String region) {
        SesUtil.region = region;
    }

    /**
     * デフォルトコンストラクタ（Spring用）
     */
    public SesUtil() {
        // Spring用のデフォルトコンストラクタ
    }

    /**
     * メール送信用コンストラクタ
     * 
     * @param recipientEmails 受信者メールアドレスのリスト
     * @param subject メール件名
     * @param body メール本文
     */
    public SesUtil(List<String> recipientEmails, String subject, String body) {
        this.recipientEmails = recipientEmails;
        this.subject = subject;
        this.body = body;

        this.sesClient = SesClient.builder()
                .region(Region.of(region != null ? region : "ap-northeast-1"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                accessKey != null ? accessKey : "YOUR_ACCESS_KEY", 
                                secretKey != null ? secretKey : "YOUR_SECRET_KEY")))
                .build();
    }

    public void send() {
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(Destination.builder().toAddresses(recipientEmails).build())
                    .source(senderEmail != null ? senderEmail : "mic-sales@inter-mic.co.jp")
                    .message(Message.builder()
                            .subject(Content.builder().charset(CHARSET).data(subject).build())
                            .body(Body.builder()
                            		.html(Content.builder().charset(CHARSET).data(body).build())
                            	.build())
                            .build())
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            System.out.println("Email sent! Message ID: " + response.messageId());

        } catch (SesException e) {
            System.err.println("The email was not sent. Error message: " + e.awsErrorDetails().errorMessage());
        } finally {
            sesClient.close();
        }
    }
}
