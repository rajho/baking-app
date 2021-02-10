package com.example.android.baky.data.network.dto;


import com.example.android.baky.data.local.models.Step;
import com.squareup.moshi.Json;

public class StepDto {
   @Json(name = "id")
   private int    sortOrder;
   private String shortDescription;
   private String description;
   private String videoURL;
   private String thumbnailURL;

   public StepDto(int sortOrder,
       String shortDescription,
       String description,
       String videoURL,
       String thumbnailURL) {
      this.sortOrder        = sortOrder;
      this.shortDescription = shortDescription;
      this.description      = description;
      this.videoURL         = videoURL;
      this.thumbnailURL     = thumbnailURL;
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

   public Step asDatabaseModel(int recipeId) {
      return new Step(sortOrder, shortDescription, description, videoURL, thumbnailURL, recipeId);
   }
}
