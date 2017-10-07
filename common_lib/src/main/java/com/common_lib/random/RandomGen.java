package com.common_lib.random;

import java.util.Random;

public class RandomGen extends Random{

	private static final long serialVersionUID = 1L;

	
	public int nextInt(int l,int h) {
		
		return this.nextInt(h-l)+l;
	}
	
}
