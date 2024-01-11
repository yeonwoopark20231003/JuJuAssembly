package com.example.jujuassembly.domain.reviewImage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.jujuassembly.domain.review.entity.Review;
import com.example.jujuassembly.domain.reviewImage.entity.ReviewImage;
import com.example.jujuassembly.domain.reviewImage.repository.ReviewImageRepository;
import com.example.jujuassembly.global.exception.ApiException;
import com.example.jujuassembly.global.s3.S3Manager;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ReviewImageServiceTest {

  @Mock
  ReviewImageRepository reviewImageRepository;
  @Mock
  S3Manager s3Manager;

  @InjectMocks
  ReviewImageService reviewImageService;

  private Long reviewId = 1L;

  private Set<ReviewImage> reviewImages;

  private MultipartFile[] images;
  private MultipartFile image1;
  private MultipartFile image2;

  @BeforeEach
  void setUp() {
    image1 = mock(MultipartFile.class);
    image2 = mock(MultipartFile.class);
    images = new MultipartFile[]{image1, image2};

    ReviewImage reviewImage1 = ReviewImage.builder().id(1L)
        .imageUrl("https://example.com/image.jpg").build();
    ReviewImage reviewImage2 = ReviewImage.builder().id(2L)
        .imageUrl("https://example.com/image.jpg").build();

    reviewImages = new LinkedHashSet<>();
    reviewImages.add(reviewImage1);
    reviewImages.add(reviewImage2);

  }

  @Test
  @DisplayName("리뷰 이미지 업로드 - 지원하지 않는 이미지 유형")
  void uploadReviewImagesUnsupportedImageTypeTest() throws Exception {
    // given
    Review review = Review.builder().id(reviewId).build();

    when(image1.getContentType()).thenReturn("application/pdf"); // set an unsupported content type

    // when, then
    ApiException exception = assertThrows(ApiException.class,
        () -> reviewImageService.uploadReviewImages(review, images));

    assertEquals("이미지 파일이 아닙니다.", exception.getMsg());
  }

  @Test
  @DisplayName("리뷰 이미지 업로드 - 저장 메소드 호출 확인")
  void uploadReviewImagesSaveMethodCallTest() throws Exception {
    // given
    Review review = Review.builder().id(reviewId).reviewImages(reviewImages).build();

    when(image1.getContentType()).thenReturn("image/jpeg");
    when(image2.getContentType()).thenReturn("image/jpeg");
    when(s3Manager.uploadMultipartFileWithPublicRead(any(), any())).thenReturn(
        "https://example.com/image.jpg");

    // when
    reviewImageService.uploadReviewImages(review, images);

    // then
    verify(reviewImageRepository, times(2)).save(any(ReviewImage.class));
  }


  @Test
  @DisplayName("리뷰 이미지 모두 삭제 테스트")
  void deleteAllReviewImagesTest() {
    // 초기화
    Mockito.reset(reviewImageRepository);

    // given
    Review review = Review.builder().id(reviewId).reviewImages(reviewImages).build();
    String dirName = "reviews";

    // when
    reviewImageService.deleteAllReviewImages(review, dirName);

    // then
    verify(reviewImageRepository, times(1)).deleteAllByReview(eq(review));
    verify(s3Manager, times(1)).deleteAllImageFiles(eq(review.getId().toString()), eq(dirName));
  }
}