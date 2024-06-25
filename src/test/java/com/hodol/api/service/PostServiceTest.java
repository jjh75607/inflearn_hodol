package com.hodol.api.service;

import com.hodol.api.domain.Post;
import com.hodol.api.repository.PostRepository;
import com.hodol.api.request.PostCreate;
import com.hodol.api.response.PostResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }


    @Test
    @DisplayName("글 작성")
    void test1() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // when
        postService.write(postCreate);

        // then
        Assertions.assertEquals(1L, postRepository.count());

        Post post = postRepository.findAll().get(0);
        Assertions.assertEquals("제목입니다.", post.getTitle());
        Assertions.assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("글 한개 조회")
    void test2() {
        // given
        Post requestPost = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        // when
        PostResponse response = postService.get(requestPost.getId());

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals("foo", response.getTitle());
        Assertions.assertEquals("bar", response.getContent());
    }

    @Test
    @DisplayName("글 1 페이지 조회")
    void test3() {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("제목 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();
        postRepository.saveAll(requestPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");

        // when
        List<PostResponse> posts = postService.getList(pageable);

        // then
        Assertions.assertEquals(5L, posts.size());
        Assertions.assertEquals("제목 30", posts.get(0).getTitle());
        Assertions.assertEquals("제목 26", posts.get(4).getTitle());
    }
}