package com.fedex.smartpost.mts.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TransferContext {
	private List<Message> messageList = new ArrayList<Message>();
	private Queue<List<Message>> stringQueue;
	private int batchSize;

	public void setStringQueue(Queue<List<Message>> stringQueue) {
		this.stringQueue = stringQueue;
	}

	public void addToList(Message message) {
		messageList.add(message);
		if (messageList.size() >= batchSize) {
			stringQueue.add(messageList);
			messageList = new ArrayList<Message>();
		}
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void completeBatch() {
		if (!messageList.isEmpty()) {
			stringQueue.add(messageList);
			messageList = new ArrayList<Message>();
		}
	}
}
