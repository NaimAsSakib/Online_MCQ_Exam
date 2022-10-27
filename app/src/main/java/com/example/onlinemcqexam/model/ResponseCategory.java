package com.example.onlinemcqexam.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseCategory{

	@SerializedName("General Knowledge")
	private List<String> generalKnowledge;

	@SerializedName("Geography")
	private List<String> geography;

	@SerializedName("Music")
	private List<String> music;

	@SerializedName("Society & Culture")
	private List<String> societyCulture;

	@SerializedName("Arts & Literature")
	private List<String> artsLiterature;

	@SerializedName("Science")
	private List<String> science;

	@SerializedName("Sport & Leisure")
	private List<String> sportLeisure;

	@SerializedName("History")
	private List<String> history;

	@SerializedName("Food & Drink")
	private List<String> foodDrink;

	@SerializedName("Film & TV")
	private List<String> filmTV;

	public List<String> getGeneralKnowledge(){
		return generalKnowledge;
	}

	public List<String> getGeography(){
		return geography;
	}

	public List<String> getMusic(){
		return music;
	}

	public List<String> getSocietyCulture(){
		return societyCulture;
	}

	public List<String> getArtsLiterature(){
		return artsLiterature;
	}

	public List<String> getScience(){
		return science;
	}

	public List<String> getSportLeisure(){
		return sportLeisure;
	}

	public List<String> getHistory(){
		return history;
	}

	public List<String> getFoodDrink(){
		return foodDrink;
	}

	public List<String> getFilmTV(){
		return filmTV;
	}
}