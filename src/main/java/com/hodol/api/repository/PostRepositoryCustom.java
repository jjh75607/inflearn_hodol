package com.hodol.api.repository;

import com.hodol.api.domain.Post;
import com.hodol.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
