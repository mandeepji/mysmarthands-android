package com.msh.common.android.dictionary;

import java.io.Serializable;
import java.util.ArrayList;

public class QuizSet implements Serializable{
	
	private static final long serialVersionUID = 1L;
	ArrayList<String> answers;
	ArrayList<String> list;
	int qSize;
	
	ArrayList<QuestionSet> qSet;

	public QuizSet(int qSize,ArrayList<String> answers, ArrayList<String> list) {
	
		this.qSize = qSize;
		this.answers = answers;
		this.list = list;
		
		qSet = new ArrayList<QuestionSet>( answers.size() );
	}
	
	public boolean hasNext(){
		
		return !answers.isEmpty();
	}
	
	public QuestionSet nextQuestion(){
		
		String answer = answers.remove(0);
		
		QuestionSet q = new QuestionSet(qSize,answer,list);
		qSet.add(q);
		
		return q;
	}
	
	public int answerCurrent(int answered){
		
		QuestionSet q = qSet.get( qSet.size()-1 );
		q.setAnswered(answered);
		
		return q.getAnswerIndex();
	}
	
	public int size(){
		
		if(answers!=null)
			return answers.size() + qSet.size();
		else
			return qSet.size();
	}
	
	public int currentQuestion(){
		
		return qSet.size()+1;
	}
	
	public String currentAnswer(){
		
		return qSet.get(qSet.size()-1).getAnswer();
	}
	
	public float tally(){
		
		float total = 0;
		for (QuestionSet q : qSet) {
			if(q.answeredCorrect())
				total++;
		}
		
		return total/qSet.size();
	}
}
