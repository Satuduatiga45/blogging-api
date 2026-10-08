package com.satuduatiga.api.blog;

import static com.satuduatiga.api.blog.mapper.BlogMapper.mapToBlogResponse;
import static com.satuduatiga.api.blog.mapper.BlogMapper.mapToTopicResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.satuduatiga.api.blog.dto.BlogRequest;
import com.satuduatiga.api.blog.dto.BlogResponse;
import com.satuduatiga.api.blog.dto.TopicResponse;
import com.satuduatiga.api.blog.entity.BlogEntity;
import com.satuduatiga.api.blog.entity.TopicEntity;
import com.satuduatiga.api.blog.repository.BlogRepository;
import com.satuduatiga.api.blog.repository.TopicRepository;
import com.satuduatiga.api.blog.service.BlogService;
import com.satuduatiga.api.common.exception.ResourceNotFoundException;
import com.satuduatiga.api.common.exception.UnauthorizedException;
import com.satuduatiga.api.user.entity.UserEntity;
import com.satuduatiga.api.user.repository.UserRepository;
import com.satuduatiga.api.user.repository.UserRoleRepository;
import com.satuduatiga.api.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService = spy(new UserService(userRepository, userRoleRepository, passwordEncoder));

    @InjectMocks
    private BlogService blogService;

    // @BeforeEach
    // void setUp() {
    // UserService instance = new UserService(userRepository, userRoleRepository,
    // passwordEncoder);
    // this.userService = spy(instance);
    // }

    // getAllTopics
    @Test
    void getAllTopics_Success() {
        TopicEntity topic1 = new TopicEntity();
        topic1.setName("topic1");
        TopicEntity topic2 = new TopicEntity();
        topic2.setName("topic2");

        List<TopicEntity> topics = List.of(topic1, topic2);
        List<TopicResponse> topicResponses = topics.stream().map(topic -> mapToTopicResponse(topic))
                .collect(Collectors.toList());

        when(topicRepository.findAll()).thenReturn(topics);

        List<TopicResponse> actualResponses = blogService.getAllTopics();

        assertNotNull(actualResponses);
        assertEquals(topicResponses, actualResponses);
    }

    @Test
    void getAllTopics_Success_Empty() {
        List<TopicResponse> topicResponses = List.of();

        when(topicRepository.findAll()).thenReturn(List.of());

        List<TopicResponse> actualResponses = blogService.getAllTopics();

        assertNotNull(actualResponses);
        assertEquals(topicResponses, actualResponses);
    }

    // getAllBlogs
    @Test
    void getAllBlogs_Success() {
        BlogEntity blog1 = new BlogEntity();
        blog1.setTitle("blog 1");
        blog1.setContent("blog 1");

        BlogEntity blog2 = new BlogEntity();
        blog2.setTitle("blog 2");
        blog2.setContent("blog 2");

        Sort sort = Sort.by("createdAt").ascending();
        Pageable pageable = PageRequest.of(1, 2, sort);
        List<BlogEntity> blogList = List.of(blog1, blog2);
        Page<BlogEntity> pagedBlog = new PageImpl<>(blogList, pageable, 2);
        Page<BlogResponse> pageResponse = pagedBlog.map(blog -> mapToBlogResponse(blog));

        when(blogRepository.findAll(eq(pageable))).thenReturn(pagedBlog);

        Page<BlogResponse> actualPage = blogService.getAllBlogs(null, 1, 2, "createdAt", "asc");

        assertNotNull(actualPage);
        assertEquals(pageResponse, actualPage);
    }

    @Test
    void getAllBlogs_Success_UsingTopics() {
        TopicEntity topic = new TopicEntity();
        topic.setName("topic");

        BlogEntity blog1 = new BlogEntity();
        blog1.setTitle("blog 1");
        blog1.setContent("blog 1");
        blog1.setTopics(Set.of(topic));

        BlogEntity blog2 = new BlogEntity();
        blog2.setTitle("blog 2");
        blog2.setContent("blog 2");
        blog2.setTopics(Set.of(topic));

        Sort sort = Sort.by("createdAt").ascending();
        Pageable pageable = PageRequest.of(1, 2, sort);
        List<BlogEntity> blogList = List.of(blog1, blog2);
        Page<BlogEntity> pagedBlog = new PageImpl<>(blogList, pageable, 2);
        Page<BlogResponse> pageResponse = pagedBlog.map(blog -> mapToBlogResponse(blog));

        ArgumentCaptor<Specification<BlogEntity>> specCaptor = ArgumentCaptor.forClass(Specification.class);

        when(blogRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pagedBlog);

        Page<BlogResponse> actualPage = blogService.getAllBlogs("topic", 1, 2, "createdAt", "asc");

        assertNotNull(actualPage);
        assertEquals(pageResponse, actualPage);
        verify(blogRepository).findAll(specCaptor.capture(), eq(pageable));

        assertNotNull(specCaptor.getValue());

    }

    // getAllBlogsByUser
    @Test
    void getAllBlogsByUser_Success() {
        BlogEntity blog1 = new BlogEntity();
        blog1.setTitle("blog 1");
        blog1.setContent("blog 1");

        BlogEntity blog2 = new BlogEntity();
        blog2.setTitle("blog 2");
        blog2.setContent("blog 2");

        String username = "user";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        Sort sort = Sort.by("createdAt").ascending();
        Pageable pageable = PageRequest.of(1, 2, sort);
        List<BlogEntity> blogList = List.of(blog1, blog2);
        Page<BlogEntity> pagedBlog = new PageImpl<>(blogList, pageable, 2);
        Page<BlogResponse> expectedResponse = pagedBlog.map(blog -> mapToBlogResponse(blog));

        when(blogRepository.findByUserUsername(username, pageable)).thenReturn(pagedBlog);

        Page<BlogResponse> actualResponse = blogService.getAllBlogsByUser(username, 1, 2, "createdAt", "asc");

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getAllBlogsByUser_Failed_UserNotFound() {
        String username = "user";

        when(userRepository.existsByUsername(username)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> blogService.getAllBlogsByUser(username, 1, 2, "createdAt", "asc"));

        assertEquals("User not found", ex.getMessage());
    }

    // getBlogById
    @Test
    void getBlogById_Success() {
        BlogEntity blog = new BlogEntity();
        blog.setTitle("blog 1");
        blog.setContent("blog 1");

        Long id = 1L;

        BlogResponse blogResponse = mapToBlogResponse(blog);

        when(blogRepository.findById(id)).thenReturn(Optional.of(blog));

        BlogResponse actualBlogResponse = blogService.getBlogById(id);

        assertNotNull(actualBlogResponse);
        assertEquals(blogResponse, actualBlogResponse);
    }

    @Test
    void getBlogById_Failed_BlogNotFound() {
        Long id = 1L;

        when(blogRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> blogService.getBlogById(id));

        assertEquals("Blog could not be found", ex.getMessage());
    }

    // createBlog
    @Test
    void createBlog_Success() {
        UserEntity user = new UserEntity();
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        TopicEntity topic1 = new TopicEntity();
        topic1.setName("topic1");
        TopicEntity topic2 = new TopicEntity();
        topic2.setName("topic2");

        BlogEntity blog = new BlogEntity();
        blog.setTitle("blog");
        blog.setContent("blog");
        blog.setTopics(Set.of(topic1, topic2));

        BlogRequest request = BlogRequest.builder()
                .title(blog.getTitle())
                .content(blog.getContent())
                .topics(Set.of(topic1.getName(), topic2.getName()))
                .build();

        doReturn(user).when(userService).getCurrentAuthenticatedUser();

        when(topicRepository.findByNameIgnoreCase(any(String.class))).thenReturn(Optional.empty());
        when(topicRepository.save(any(TopicEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BlogResponse actualResponse = blogService.createBlog(request);

        assertNotNull(actualResponse);
        assertEquals(blog.getTitle(), actualResponse.getTitle());
        assertEquals(blog.getContent(), actualResponse.getContent());
        assertEquals(Set.of(topic1.getName(), topic2.getName()), actualResponse.getTopics());

        verify(blogRepository, times(1)).save(any(BlogEntity.class));

    }

    // updateBlog
    @Test
    void updateBlog_Success() {
        Long id = 1L;

        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        TopicEntity topic1 = new TopicEntity();
        topic1.setName("topic1");
        TopicEntity topic2 = new TopicEntity();
        topic2.setName("topic2");

        BlogEntity blog = new BlogEntity();
        blog.setId(id);
        blog.setTitle("blog");
        blog.setContent("blog");
        blog.setTopics(Set.of(topic1, topic2));
        blog.setUser(user);

        BlogRequest request = BlogRequest.builder()
                .title(blog.getTitle())
                .content(blog.getContent())
                .topics(Set.of(topic1.getName(), topic2.getName()))
                .build();

        doReturn(user).when(userService).getCurrentAuthenticatedUser();
        when(blogRepository.findById(id)).thenReturn(Optional.of(blog));
        when(blogRepository.save(any(BlogEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(topicRepository.save(any(TopicEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BlogResponse actualResponse = blogService.updateBlog(id, request);

        assertNotNull(actualResponse);
        assertEquals(blog.getTitle(), actualResponse.getTitle());
        assertEquals(blog.getContent(), actualResponse.getContent());
        assertEquals(Set.of(topic1.getName(), topic2.getName()), actualResponse.getTopics());

        verify(blogRepository, times(1)).save(any(BlogEntity.class));

    }

    @Test
    void updateBlog_Failed_BlogNotFound() {
        Long id = 1L;

        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        TopicEntity topic1 = new TopicEntity();
        topic1.setName("topic1");
        TopicEntity topic2 = new TopicEntity();
        topic2.setName("topic2");

        BlogEntity blog = new BlogEntity();
        blog.setId(id);
        blog.setTitle("blog");
        blog.setContent("blog");
        blog.setTopics(Set.of(topic1, topic2));
        blog.setUser(user);

        BlogRequest request = BlogRequest.builder()
                .title(blog.getTitle())
                .content(blog.getContent())
                .topics(Set.of(topic1.getName(), topic2.getName()))
                .build();

        doReturn(user).when(userService).getCurrentAuthenticatedUser();
        when(blogRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> blogService.updateBlog(id, request));

        assertEquals("Update failed. Blog could not be found", ex.getMessage());
    }

    @Test
    void updateBlog_Failed_UnauthorizedUser() {
        Long id = 1L;
        Long fakeId = 2L;

        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        UserEntity fakeUser = new UserEntity();
        user.setId(fakeId);

        TopicEntity topic1 = new TopicEntity();
        topic1.setName("topic1");
        TopicEntity topic2 = new TopicEntity();
        topic2.setName("topic2");

        BlogEntity blog = new BlogEntity();
        blog.setId(id);
        blog.setTitle("blog");
        blog.setContent("blog");
        blog.setTopics(Set.of(topic1, topic2));
        blog.setUser(user);

        BlogRequest request = BlogRequest.builder()
                .title(blog.getTitle())
                .content(blog.getContent())
                .topics(Set.of(topic1.getName(), topic2.getName()))
                .build();

        doReturn(fakeUser).when(userService).getCurrentAuthenticatedUser();
        when(blogRepository.findById(id)).thenReturn(Optional.of(blog));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> blogService.updateBlog(id, request));

        assertEquals("You do not have permission to edit this blog", ex.getMessage());
    }

    // deleteBlog
    @Test
    void deleteBlog_Success() {
        Long id = 1L;

        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        TopicEntity topic1 = new TopicEntity();
        topic1.setName("topic1");
        TopicEntity topic2 = new TopicEntity();
        topic2.setName("topic2");

        BlogEntity blog = new BlogEntity();
        blog.setId(id);
        blog.setTitle("blog");
        blog.setContent("blog");
        blog.setTopics(Set.of(topic1, topic2));
        blog.setUser(user);

        doReturn(user).when(userService).getCurrentAuthenticatedUser();
        when(blogRepository.findById(id)).thenReturn(Optional.of(blog));

        blogService.deleteBlog(id);

        verify(blogRepository, times(1)).delete(any(BlogEntity.class));

    }

    @Test
    void deleteBlog_Failed_BlogNotFound() {
        Long id = 1L;

        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        TopicEntity topic1 = new TopicEntity();
        topic1.setName("topic1");
        TopicEntity topic2 = new TopicEntity();
        topic2.setName("topic2");

        BlogEntity blog = new BlogEntity();
        blog.setId(id);
        blog.setTitle("blog");
        blog.setContent("blog");
        blog.setTopics(Set.of(topic1, topic2));
        blog.setUser(user);

        doReturn(user).when(userService).getCurrentAuthenticatedUser();
        when(blogRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> blogService.deleteBlog(id));

        assertEquals("Delete failed. Blog could not be found", ex.getMessage());
    }

    @Test
    void deleteBlog_Failed_UnauthorizedUser() {
        Long id = 1L;
        Long fakeId = 2L;

        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        UserEntity fakeUser = new UserEntity();
        user.setId(fakeId);

        TopicEntity topic1 = new TopicEntity();
        topic1.setName("topic1");
        TopicEntity topic2 = new TopicEntity();
        topic2.setName("topic2");

        BlogEntity blog = new BlogEntity();
        blog.setId(id);
        blog.setTitle("blog");
        blog.setContent("blog");
        blog.setTopics(Set.of(topic1, topic2));
        blog.setUser(user);

        doReturn(fakeUser).when(userService).getCurrentAuthenticatedUser();
        when(blogRepository.findById(id)).thenReturn(Optional.of(blog));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> blogService.deleteBlog(id));

        assertEquals("You do not have permission to delete this blog", ex.getMessage());
    }

    // deleteBlogAsAdmin
    @Test
    void deleteBlogAsAdmin_Success() {
        Long id = 1L;

        TopicEntity topic1 = new TopicEntity();
        topic1.setName("topic1");
        TopicEntity topic2 = new TopicEntity();
        topic2.setName("topic2");

        BlogEntity blog = new BlogEntity();
        blog.setId(id);
        blog.setTitle("blog");
        blog.setContent("blog");
        blog.setTopics(Set.of(topic1, topic2));

        when(blogRepository.findById(id)).thenReturn(Optional.of(blog));

        blogService.deleteBlogAsAdmin(id);

        verify(blogRepository, times(1)).delete(any(BlogEntity.class));
    }

    @Test
    void deleteBlogAsAdmin_Failed_BlogNotFound() {
        Long id = 1L;

        TopicEntity topic1 = new TopicEntity();
        topic1.setName("topic1");
        TopicEntity topic2 = new TopicEntity();
        topic2.setName("topic2");

        BlogEntity blog = new BlogEntity();
        blog.setId(id);
        blog.setTitle("blog");
        blog.setContent("blog");
        blog.setTopics(Set.of(topic1, topic2));

        when(blogRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> blogService.deleteBlogAsAdmin(id));

        assertEquals("Delete failed. Blog could not be found", ex.getMessage());
    }
}
