package com.common_lib.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class TaskQueue<T extends Runnable> {
	
	private TaskQueueListener<T> listener;
	
	// uses threadPriorityQueue OR threadQueue depending on constructor
	private PriorityQueue<TaskQueueThread> threadPriorityQueue;
	private List<TaskQueueThread> threadQueue;
	
	private List<TaskQueueThread> threadsRunning;
	
	private int maxRunning;
	private boolean isRunning;

	// regular Queue
	public TaskQueue(int maxRunning,TaskQueueListener<T> listener){
		
		this(false,maxRunning,listener);
	}
	
	// choice of priority or regular
	public TaskQueue(
			boolean priorityQueue,
			int maxRunning,
			TaskQueueListener<T> listener){
		
		this.maxRunning = maxRunning;
		this.isRunning = false;
		this.listener = listener;
		
		if(priorityQueue){
			threadPriorityQueue = new PriorityQueue<TaskQueueThread>();
		}
		else{
			threadQueue = new ArrayList<TaskQueueThread>();
		}
		
		threadsRunning = new ArrayList<TaskQueueThread>(this.maxRunning);
		
	}
	
	// Priority Queue
	public TaskQueue(
			Iterable<T> tasks,
			Iterable<Double> priorities,
			int maxRunning,
			TaskQueueListener<T> listener) {
	
		this.maxRunning = maxRunning;
		this.isRunning = false;
		this.listener = listener;
		
		threadPriorityQueue = new PriorityQueue<TaskQueueThread>();
		Iterator<T> taskIT = tasks.iterator();
		Iterator<Double> priorityIT = priorities.iterator();
		
		try {
			while (taskIT.hasNext()) {
				threadPriorityQueue.add( new TaskQueueThread(taskIT.next(),priorityIT.next()) );
			}
		} catch (Exception e) {
			throw new RuntimeException("number of tasks != number of priorities");
		}
		if(priorityIT.hasNext()){
			throw new RuntimeException("number of tasks != number of priorities");
		}
		
		
		threadsRunning = new ArrayList<TaskQueueThread>(this.maxRunning);
	}
	
	// Regular Queue
	public TaskQueue(
			Iterable<T> tasks,
			int maxRunning,
			TaskQueueListener<T> listener) {
	
		this.maxRunning = maxRunning;
		this.isRunning = false;
		this.listener = listener;
		
		threadQueue = new ArrayList<TaskQueueThread>();
		for (T task : tasks) {
			threadQueue.add( new TaskQueueThread(task) );
		}
		
		threadsRunning = new ArrayList<TaskQueueThread>(this.maxRunning);
	}
	
	public void start(){
		
		this.isRunning = true;
		dequeueTasks();
		listener.onQueueStarted(this);
	}
	
	// pause does not halt threads but inhibits further dequeueing
	public void pause(){
		
		this.isRunning = false;
		listener.onQueuePaused(this);
	}
	
	public boolean isRunning(){
		
		return isRunning;
	}
	
	protected void queueFinished(){
		
		this.isRunning = false;
		if(listener !=null){
			listener.onQueueEnded(this);
		}
	}
	
	//-----------------------------------------------------+
	// dual-queue-type interface
	private TaskQueueThread poll(){
		
		return (threadPriorityQueue ==null)
				? threadQueue.remove(0)
				: threadPriorityQueue.poll();
	}
	
	private boolean queueIsEmpty(){
		
		return (threadPriorityQueue ==null)
				? threadQueue.isEmpty()
				: threadPriorityQueue.isEmpty();
	}
	
	public int getSize(){
	
		int running = (threadsRunning ==null)
						? 0
						: threadsRunning.size();
		
		return (threadPriorityQueue ==null)
				? threadQueue.size()+running
				: threadPriorityQueue.size()+running;
	}
	
	//-----------------------------------------------------+
	// synchronized methods
	synchronized
	protected void dequeueTasks(){
		
		if(!isRunning){
			return;
		}
		
		TaskQueueThread nextTask;
		while(!this.queueIsEmpty() && 
				threadsRunning.size() < maxRunning){
			nextTask = this.poll();
			nextTask.start();
			threadsRunning.add(nextTask);
		}
	}
	
	synchronized
	protected void taskCompleted(TaskQueueThread thread){
		
		threadsRunning.remove(thread);
		
		if(listener !=null){
			boolean restartTask = listener.taskStopped( thread.task );
			if(restartTask){
				addTask(thread.task,true);
			}
		}
		
		if(this.queueIsEmpty() && 
				threadsRunning.isEmpty()){
			queueFinished();
		}
		else{
			dequeueTasks();
		}
	}
	
	public void addTask(T task,boolean runIfStopped){
		
		if(threadQueue ==null){
			throw new IllegalStateException("use addTask(Runnable,double,boolean) when using priorityQueue.");
		}
		
		threadQueue.add( new TaskQueueThread(task) );
		if(!isRunning && runIfStopped){
			this.start();
		}
	}
	
	public void addTask(T task,double priority,boolean runIfStopped){
		
		if(threadQueue ==null){
			throw new IllegalStateException("use addTask(Runnable,boolean) when using a regular Queue or switch");
		}
		
		threadPriorityQueue.add( new TaskQueueThread(task,priority) );
		if(!isRunning && runIfStopped){
			this.start();
		}
	}
	
	//-----------------------------------------------------+
	private class TaskQueueThread extends Thread implements Comparable<TaskQueueThread>{
		
		double priority;
		T task; // because there is no Thread.getRunnable()
		
		public TaskQueueThread(T task) {
			
			super(task);
			this.task = task;
			this.priority = 0;
		}
		
		public TaskQueueThread(T task,double priority) {
			
			super(task);
			this.task = task;
			this.priority = priority;
		}
		
		@Override
		public void run() {
			
			super.run(); // runs the task passed in constructor
			TaskQueue.this.taskCompleted(this);
		}

		@Override
		public int compareTo(TaskQueueThread that) {
		
			return Double.compare(this.priority, that.priority);
		}
	}
	
	//-----------------------------------------------------+
	public interface TaskQueueListener<T extends Runnable>{
		
		public boolean taskStopped(T task);
		
		public void onQueueStarted(TaskQueue<T> queue);
		
		public void onQueuePaused(TaskQueue<T> queue);
		
		public void onQueueEnded(TaskQueue<T> queue);
	}
	
	//-----------------------------------------------------+
}
