package com.example.jujuassembly.global;

import com.example.jujuassembly.domain.category.entity.Category;

public interface CategoryTestUtil {

  String ANOTHER_PREDIX = "another-";
  Long TEST_CATEGORY_ID = 1L;
  Long TEST_ANOTHER_CATEGORY_ID = 2L;
  String TEST_CATEGORY_NAME = "name";


  Category TEST_CATEGORY = Category.builder()
      .id(TEST_CATEGORY_ID)
      .name(TEST_CATEGORY_NAME)
      .build();

  Category TEST_ANOTHER_CATEGORY = Category.builder()
      .id(TEST_ANOTHER_CATEGORY_ID)
      .name(ANOTHER_PREDIX+TEST_CATEGORY_NAME)
      .build();

}
