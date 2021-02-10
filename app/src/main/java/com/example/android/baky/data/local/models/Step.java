package com.example.android.baky.data.local.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Recipe.class,
                                  parentColumns = "id",
                                  childColumns = "recipe_id"),
        indices = {
            @Index("recipe_id"),
            @Index(value = { "sort_order", "recipe_id" }, unique = true)
        }
)
public class Step {

  @PrimaryKey(autoGenerate = true)
  private int id;

  @ColumnInfo(name = "sort_order")
  private int sortOrder;

  @ColumnInfo(name = "short_description")
  private String shortDescription;

  private String description;

  @ColumnInfo(name = "video_url")
  private String videoURL;

  @ColumnInfo(name = "thumbnail_url")
  private String thumbnailURL;

  @ColumnInfo(name = "recipe_id")
  private int recipeId;


  public Step(int id,
      int sortOrder,
      String shortDescription,
      String description,
      String videoURL,
      String thumbnailURL,
      int recipeId) {
    this.id               = id;
    this.sortOrder        = sortOrder;
    this.shortDescription = shortDescription;
    this.description      = description;
    this.videoURL         = videoURL;
    this.thumbnailURL     = thumbnailURL;
    this.recipeId         = recipeId;
  }

  @Ignore
  public Step(int sortOrder,
      String shortDescription,
      String description,
      String videoURL,
      String thumbnailURL,
      int recipeId) {
    this.sortOrder        = sortOrder;
    this.shortDescription = shortDescription;
    this.description      = description;
    this.videoURL         = videoURL;
    this.thumbnailURL     = thumbnailURL;
    this.recipeId         = recipeId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(int sortOrder) {
    this.sortOrder = sortOrder;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getVideoURL() {
    return videoURL;
  }

  public void setVideoURL(String videoURL) {
    this.videoURL = videoURL;
  }

  public String getThumbnailURL() {
    return thumbnailURL;
  }

  public void setThumbnailURL(String thumbnailURL) {
    this.thumbnailURL = thumbnailURL;
  }

  public int getRecipeId() {
    return recipeId;
  }

  public void setRecipeId(int recipeId) {
    this.recipeId = recipeId;
  }
}
