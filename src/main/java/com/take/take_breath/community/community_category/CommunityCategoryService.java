// CommunityCategoryService.java

package com.take.take_breath.community.community_category;

import com.take.take_breath._core._exception.Exception404;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCategoryService {

    private final CommunityCategoryRepository communityCategoryRepository;

    /**
     * 전체 카테고리 목록 조회
     */
    public List<CommunityCategoryResponse.ListDTO> findAllCategories() {
        return communityCategoryRepository.findAll().stream()
                .map(category -> new CommunityCategoryResponse.ListDTO(category))
                .toList();
    }

    /**
     * 카테고리 상세 조회
     */
    public CommunityCategoryResponse.ListDTO findCategoryById(Long id) {
        CommunityCategory category = communityCategoryRepository.findById(id)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다. ID: " + id));

        return new CommunityCategoryResponse.ListDTO(category);
    }

    /**
     * 카테고리 생성 (관리자 전용)
     */
    @Transactional
    public CommunityCategoryResponse.ResponseDTO saveCategory(CommunityCategoryRequest.SaveDTO saveDTO, Long adminId) {

        CommunityCategory category = CommunityCategory.builder()
                .name(saveDTO.getName())
                .build();

        CommunityCategory savedCategory = communityCategoryRepository.save(category);
        log.info("[카테고리 생성] adminId={}, categoryId={}, name={}", adminId, savedCategory.getId(), savedCategory.getName());
        return new CommunityCategoryResponse.ResponseDTO(savedCategory);
    }

    /**
     * 카테고리 수정 (관리자 전용)
     */
    @Transactional
    public CommunityCategoryResponse.ResponseDTO updateCategory(Long id, CommunityCategoryRequest.UpdateDTO updateDTO, Long adminId) {
        CommunityCategory category = communityCategoryRepository.findById(id)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다. ID: " + id));

        category.update(updateDTO.getName());
        log.info("[카테고리 수정] adminId={}, categoryId={}, name={}", adminId, id, updateDTO.getName());
        return new CommunityCategoryResponse.ResponseDTO(category);
    }

    /**
     * 카테고리 삭제 (관리자 전용)
     */
    @Transactional
    public void deleteCategory(Long id, Long adminId) {
        CommunityCategory category = communityCategoryRepository.findById(id)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다. ID: " + id));

        communityCategoryRepository.delete(category);
        log.info("[카테고리 삭제] adminId={}, categoryId={}, name={}", adminId, id, category.getName());
    }
}