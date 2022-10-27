package com.example.onlinemcqexam.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Response{

	@SerializedName("difficulty")
	private String difficulty;

	@SerializedName("incorrectAnswers")
	private List<String> incorrectAnswers;

	@SerializedName("regions")
	private List<Object> regions;

	@SerializedName("question")
	private String question;

	@SerializedName("id")
	private String id;

	@SerializedName("category")
	private String category;

	@SerializedName("correctAnswer")
	private String correctAnswer;

	@SerializedName("type")
	private String type;

	@SerializedName("tags")
	private List<String> tags;

	public String getDifficulty(){
		return difficulty;
	}

	public List<String> getIncorrectAnswers(){
		return incorrectAnswers;
	}

	public List<Object> getRegions(){
		return regions;
	}

	public String getQuestion(){
		return question;
	}

	public String getId(){
		return id;
	}

	public String getCategory(){
		return category;
	}

	public String getCorrectAnswer(){
		return correctAnswer;
	}

	public String getType(){
		return type;
	}

	public List<String> getTags(){
		return tags;
	}
}