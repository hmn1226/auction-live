package com.auctionmachine.resources.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.auctionmachine.resources.model.EntryModel;

@Repository
public class EntryRepository extends BaseRepository{
	public EntryRepository() {
		super("entry");
	}
	public List<EntryModel> get(){
		return super.findAll(EntryModel.class,super.getPrefix()+":");
	}
	
	public EntryModel getById(String entryId) throws Exception{
		System.out.println(super.getPrefix()+":"+entryId);
		return super.findById(EntryModel.class,super.getPrefix()+":"+entryId);
	}
	
	public void put(EntryModel entryModel) {
		super.save(entryModel);
	}
	public void put(List<EntryModel> entryModels) {
		super.save(entryModels);
	}
	public void delete(String entryId) {
		super.deleteById("entry:"+entryId);
	}
}
