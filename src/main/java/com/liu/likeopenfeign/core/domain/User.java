package com.liu.likeopenfeign.core.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private int id;
    private String name;
    private int age;
}
