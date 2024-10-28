package com.sparta.myselectshop.naver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONObject;

@Getter
@NoArgsConstructor
public class ItemDto {
    private String title;
    private String link;
    private String image;
    private int lprice;

    @SneakyThrows
    public ItemDto(JSONObject itemJson) {
        this.title = itemJson.getString("title");
        this.link = itemJson.getString("link");
        this.image = itemJson.getString("image");
        this.lprice = itemJson.getInt("lprice");
    }
}