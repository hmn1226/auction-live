package com.auctionmachine.resources.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.auctionmachine.resources.model.UserModel;

@Repository
public class UserRepository extends BaseRepository{
	public UserRepository() {
		super("user");
	}
	public UserModel getById(String ulid) throws Exception{
		return super.findById(UserModel.class,super.getPrefix()+":"+ulid);
	}
	public UserModel getByEmail(String email) {
		for(UserModel userModel : this.getAll()) {
			if(email.equals(userModel.getEmail())) {
				return userModel;
			}
		}
		return null;
	}
    public List<UserModel> getAll() {
    	return super.findAll(UserModel.class,super.getPrefix()+":");
    }
    public void save(UserModel userModel) {
        super.save(userModel);
    }
    public void deleteById(String ulid) {
        super.deleteById("user:" + ulid);
    }
    public void deleteByEmail(String email) {
    	for(UserModel userModel : this.getAll()) {
			if(email.equals(userModel.getEmail())) {
				this.deleteById(userModel.getKey());
			}
		}
    }
}