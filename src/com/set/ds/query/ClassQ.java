package com.set.ds.query;

import java.util.ArrayList;

import com.set.ds.query.inter.QLLiteral;

//each Qi is a sequence of query literals separated by white space and
//optionally surrounded by parentheses. A query literal is one of the following:
//1. a single token [NormalLiteral]
//2. a sequence of tokens that are within double quotes, representing a phrase query [PhraseLiteral]

public class ClassQ {
	private ArrayList<QLLiteral> queryLiterals = new ArrayList<>();

	public ArrayList<QLLiteral> getQueryLiterals() {
		return queryLiterals;
	}

	public void setQueryLiteral(QLLiteral queryLiteral) {
		this.queryLiterals.add(queryLiteral);
	}
	
}
