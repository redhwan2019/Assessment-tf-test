package com.tecforte.blog.web.rest;

import com.tecforte.blog.service.BlogService;
import com.tecforte.blog.web.rest.errors.BadRequestAlertException;
import com.tecforte.blog.service.dto.BlogDTO;
import com.tecforte.blog.service.dto.EntryDTO;

import com.tecforte.blog.service.EntryService;
import org.apache.commons.lang3.StringUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link com.tecforte.blog.domain.Blog}.
 */
@RestController
@RequestMapping("/api")
public class BlogResource {

    private final Logger log = LoggerFactory.getLogger(BlogResource.class);

    private static final String ENTITY_NAME = "blog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BlogService blogService;
    private final EntryService entryService;

    public BlogResource(BlogService blogService, EntryService entryService) {
        this.blogService = blogService;
        this.entryService = entryService;
    }

    /**
     * {@code POST  /blogs} : Create a new blog.
     *
     * @param blogDTO the blogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new blogDTO, or with status {@code 400 (Bad Request)} if the
     *         blog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/blogs")
    public ResponseEntity<BlogDTO> createBlog(@Valid @RequestBody BlogDTO blogDTO) throws URISyntaxException {
        log.debug("REST request to save Blog : {}", blogDTO);
        if (blogDTO.getId() != null) {
            throw new BadRequestAlertException("A new blog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BlogDTO result = blogService.save(blogDTO);
        return ResponseEntity
                .created(new URI("/api/blogs/" + result.getId())).headers(HeaderUtil
                        .createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * {@code PUT  /blogs} : Updates an existing blog.
     *
     * @param blogDTO the blogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated blogDTO, or with status {@code 400 (Bad Request)} if the
     *         blogDTO is not valid, or with status
     *         {@code 500 (Internal Server Error)} if the blogDTO couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/blogs")
    public ResponseEntity<BlogDTO> updateBlog(@Valid @RequestBody BlogDTO blogDTO) throws URISyntaxException {
        log.debug("REST request to update Blog : {}", blogDTO);
        if (blogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BlogDTO result = blogService.save(blogDTO);
        return ResponseEntity.ok().headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, blogDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code GET  /blogs} : get all the blogs.
     *
     * 
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of blogs in body.
     */
    @GetMapping("/blogs")
    public List<BlogDTO> getAllBlogs() {
        log.debug("REST request to get all Blogs");
        return blogService.findAll();
    }

    /**
     * {@code GET  /blogs/:id} : get the "id" blog.
     *
     * @param id the id of the blogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the blogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/blogs/{id}")
    public ResponseEntity<BlogDTO> getBlog(@PathVariable Long id) {
        log.debug("REST request to get Blog : {}", id);
        Optional<BlogDTO> blogDTO = blogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(blogDTO);
    }

    /**
     * {@code DELETE  /blogs/:id} : delete the "id" blog.
     *
     * @param id the id of the blogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        log.debug("REST request to delete Blog : {}", id);
        blogService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
    }

    @DeleteMapping("/blogs/clean")
    public ResponseEntity<Void> cleanBlogs(@Valid @RequestBody String[] keywords, Pageable pageable) {
        log.debug("REST request to delete Blog : {}", keywords[0]);
        Page<EntryDTO> page = entryService.findAll(pageable);
        for (EntryDTO entry : page.getContent()) {
            for (String keyword : keywords) {
                if (entry.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || entry.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                    entryService.delete(entry.getId());
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/blogs/{id}/clean")
    public ResponseEntity<Void> cleanBlog(@PathVariable Long id, @Valid @RequestBody String[] keywords,
            Pageable pageable) {
        log.debug("REST request to clean Blog id : {}", id);
        log.debug("REST request to clean Blog : {}", keywords[0]);
        Page<EntryDTO> page = entryService.findAll(pageable);
        List<EntryDTO> blogEntries = (page.getContent()).stream().filter(e -> e.getBlogId().equals(id)).collect(Collectors.toList());
        log.debug("REST request to clean Blog : {}", blogEntries.size());
        for (EntryDTO entry : blogEntries) {
            for (String keyword : keywords) {
                if (entry.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || entry.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                    entryService.delete(entry.getId());
                }
            }
        }
        return ResponseEntity.ok().build();
    }

}
