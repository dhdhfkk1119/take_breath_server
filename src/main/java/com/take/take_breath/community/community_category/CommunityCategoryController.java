package com.take.take_breath.community.community_category;

import com.take.take_breath._core._utils.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/categories")
@RequiredArgsConstructor
@Slf4j
public class CommunityCategoryController {

    private final CommunityCategoryService communityCategoryService;

    /**
     * 전체 카테고리 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiUtil.ApiResult<List<CommunityCategoryResponse.ListDTO>>> findAllCategories() {
        List<CommunityCategoryResponse.ListDTO> categories = communityCategoryService.findAllCategories();
        log.info("[카테고리 목록 조회] count={}", categories.size());
        return ResponseEntity.ok(ApiUtil.success(categories));
    }

    /**
     * 카테고리 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiUtil.ApiResult<CommunityCategoryResponse.ListDTO>> findCategoryById(
            @PathVariable Long id) {

        CommunityCategoryResponse.ListDTO category = communityCategoryService.findCategoryById(id);
        log.info("[카테고리 조회] id={}, name={}", id, category.getName());
        return ResponseEntity.ok(ApiUtil.success(category));
    }

    /**
     * 카테고리 생성 (관리자 전용)
     * TODO: JWT 인증 구현 후 @Auth(roles = {Role.ADMIN}) 추가
     */
    @PostMapping("/admin")
    public ResponseEntity<ApiUtil.ApiResult<CommunityCategoryResponse.ResponseDTO>> saveCategory(
            @Valid @RequestBody CommunityCategoryRequest.SaveDTO saveDTO,
            @RequestParam Long adminId) {

        CommunityCategoryResponse.ResponseDTO savedCategory = communityCategoryService.saveCategory(saveDTO, adminId);
        log.info("[카테고리 생성] adminId={}, categoryId={}, name={}", adminId, savedCategory.getId(), savedCategory.getName());
        return ResponseEntity.ok(ApiUtil.success(savedCategory));
    }

    /**
     * 카테고리 수정 (관리자 전용)
     */
    @PutMapping("/{id}/admin")
    public ResponseEntity<ApiUtil.ApiResult<CommunityCategoryResponse.ResponseDTO>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CommunityCategoryRequest.UpdateDTO updateDTO,
            @RequestParam Long adminId) {

        CommunityCategoryResponse.ResponseDTO updatedCategory = communityCategoryService.updateCategory(id, updateDTO, adminId);
        log.info("[카테고리 수정] adminId={}, categoryId={}, name={}", adminId, id, updatedCategory.getName());
        return ResponseEntity.ok(ApiUtil.success(updatedCategory));
    }

    /**
     * 카테고리 삭제 (관리자 전용)
     */
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<ApiUtil.ApiResult<String>> deleteCategory(@PathVariable Long id, @RequestParam Long adminId) {
        communityCategoryService.deleteCategory(id, adminId);
        log.info("[카테고리 삭제] adminId={}, categoryId={}", adminId, id);
        return ResponseEntity.ok(ApiUtil.success("카테고리가 삭제되었습니다."));
    }
}