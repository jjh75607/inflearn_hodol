package com.hodol.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hodol.api.domain.Post;
import com.hodol.api.repository.PostRepository;
import com.hodol.api.request.PostCreate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest // 간단한 컨트롤러의 레이어 테스트에는 괜찮다
@AutoConfigureMockMvc // MockMvc 테스트를 위해
@SpringBootTest //웹의 전반적인 테스트를 위해
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    // 테스트 메소드들이 실행 되기 전 수행되는 것
    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /post 요청 Hello world 출력")
    void test() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);// json 형태로 가공

        // expected 기대값
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())//상태코드 200 기대
                .andExpect(content().string(""))//리턴 기대 값
                .andDo(print());//http 요청 summary 남기기
    }

    @Test
    @DisplayName("POST /post 요청시 타이틀 값은 필수")
    void test2() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title(null)
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);// json 형태로 가공

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("타이틀을 입력해주세요."))
                .andDo(print());//http 요청 summary 남기기
    }

    @Test
    @DisplayName("POST /post 요청시 db에 값이 저장된다")
    void test3() throws Exception {
        // given -> 어떤 데이터가 주어질때
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);// json 형태로 가공

        // when -> 이런 요청을 했을 경우
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then -> 이런 결과가 나온다
        Assertions.assertEquals(1L, postRepository.count()); // 기대값, 실제값

        Post post = postRepository.findAll().get(0);
        Assertions.assertEquals("제목입니다.", post.getTitle());
        Assertions.assertEquals("내용입니다.", post.getContent());

    }

    @Test
    @DisplayName("GET 글 한개 조회")
    void test4() throws Exception {
        // given
        Post post = Post.builder()
                .title("123456789012345")
                .content("bar")
                .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("1234567890"))
                .andExpect(jsonPath("$.content").value("bar"))
                .andDo(print());
    }

    @Test
    @DisplayName("GET 글 여러개 조회")
    void test5() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("제목 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();
        postRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/posts?page=1&sort=id,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(5)))
                .andExpect(jsonPath("$.[0].id").value(30))
                .andExpect(jsonPath("$.[0].title").value("제목 30"))
                .andExpect(jsonPath("$.[0].content").value("내용 30"))
                .andDo(print());
    }
}