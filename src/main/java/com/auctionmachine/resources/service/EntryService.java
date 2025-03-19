package com.auctionmachine.resources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auctionmachine.resources.model.EntryModel;
import com.auctionmachine.resources.repository.EntryRepository;
import com.auctionmachine.resources.schema.entry.EntriesResponse;
import com.auctionmachine.resources.schema.entry.EntryResponse;

@Service
public class EntryService {
	
	@Autowired
	EntryRepository entryRepository;
	
	public EntriesResponse get(){
		
		
		EntriesResponse ret = new EntriesResponse();
		return ret;
	}
	
	public EntryResponse getById(String entryId) {
		
		EntryResponse ret = new EntryResponse();
		return ret;
	}
	
	public void put(EntryModel entryModel) {
		this.entryRepository.put(entryModel);
	}
	
	public void delete(String entryId) {
		this.entryRepository.delete(entryId);
	}
}
