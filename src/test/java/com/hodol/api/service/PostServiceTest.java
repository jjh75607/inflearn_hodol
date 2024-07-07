package com.hodol.api.service;

import com.hodol.api.domain.Post;
import com.hodol.api.exception.PostNotFound;
import com.hodol.api.repository.PostRepository;
import com.hodol.api.request.PostCreate;
import com.hodol.api.request.PostEdit;
import com.hodol.api.request.PostSearch;
import com.hodol.api.response.PostResponse;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    @DisplayName("글 여러개 조회")
    void test3() {
        // given
        List<Post> requestPosts = IntStream.range(0, 20)
            .mapToObj(i -> Post.builder()
                .title("foo" + i)
                .content("bar" + i)
                .build())
            .toList();
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
            .page(1)
            .build();

        // when
        List<PostResponse> posts = postService.getList(postSearch);

        // then
        Assertions.assertEquals(10L, posts.size());
        Assertions.assertEquals("foo19", posts.get(0).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test4() {
        // given
        Post post = Post.builder()
            .title("foo")
            .content("bar")
            .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
            .title("FOO")
            .content("bar")
            .build();

        // when
        postService.edit(post.getId(), postEdit);

        // then
        Post changedPost = postRepository.findById(post.getId())
            .orElseThrow(() -> new RuntimeException("글이 존재 하지 않습니다. id=" + post.getId()));
        Assertions.assertEquals("FOO", changedPost.getTitle());
    }

    @Test
    @DisplayName("글 내용 수정")
    void test5() {
        // given
        Post post = Post.builder()
            .title("foo")
            .content("bar")
            .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
            .title("foo")
            .content("BAR")
            .build();

        // when
        postService.edit(post.getId(), postEdit);

        // then
        Post changedPost = postRepository.findById(post.getId())
            .orElseThrow(() -> new RuntimeException("글이 존재 하지 않습니다. id=" + post.getId()));
        Assertions.assertEquals("BAR", changedPost.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test6() {
        // given
        Post post = Post.builder()
            .title("foo")
            .content("bar")
            .build();

        postRepository.save(post);

        // when
        postService.delete(post.getId());

        // then
        Assertions.assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("글 한개 조회 - 실패 케이스")
    void test7() {
        // given
        Post post = Post.builder()
            .title("foo")
            .content("bar")
            .build();
        postRepository.save(post);

        // expected
        Assertions.assertThrows(PostNotFound.class, () -> {
            postService.get(post.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 한개 삭제 실패 케이스")
    void test8() {
        // given
        Post post = Post.builder()
            .title("foo")
            .content("bar")
            .build();
        postRepository.save(post);


        // expected
        Assertions.assertThrows(PostNotFound.class, () -> {
            postService.delete(post.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 내용 수정 - 존재하지 않는 글")
    void test9() {
        // given
        Post post = Post.builder()
            .title("foo")
            .content("bar")
            .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
            .title("foo")
            .content("BAR")
            .build();

        // expected
        Assertions.assertThrows(PostNotFound.class, () -> {
            postService.edit(post.getId() + 1L, postEdit);
        });
    }
}