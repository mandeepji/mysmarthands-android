package com.msh.common.android.dictionary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class QuestionSet implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private ArrayList<String> list;
	private int ansIndex = -2;
	private int answeredIndex = -1;

	public QuestionSet(int size, String answer, ArrayList<String> words) {

		list = new ArrayList<String>(size);
		words.remove(answer);
		Collections.shuffle(words);

		list.addAll(words.subList(0,size-1));
		list.add(answer);
		Collections.shuffle(list);

		ansIndex = list.indexOf(answer);
		words.add(answer);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getSet(){
		
		return (ArrayList<String>) list.clone();
	}

	public void setAnswered(int answer){
		
		answeredIndex = answer;
	}
	
	public int getAnswerIndex() {

		return ansIndex;
	}

	public String getAnswer() {

		return list.get(ansIndex);
	}
	
	public String getAnswered(){
		
		return list.get(answeredIndex);
	}
	
	public String wordAtIndex(int index) {

		return list.get(index);
	}

	public int size() {

		return list.size();
	}
	
	public boolean answeredCorrect(){
		
		return (ansIndex == answeredIndex);
	}

}
