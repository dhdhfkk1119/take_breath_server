package com.take.take_breath.counselor.like;

import com.take.take_breath.counselor.Counselor;
import com.take.take_breath.members.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CounselorLikeRepository extends JpaRepository<CounselorLike, Long> {

    Optional<CounselorLike> findByMemberAndCounselor(Member member, Counselor counselor);

    Page<CounselorLike> findAllByMember(Member member, Pageable pageable);

    long countByCounselor(Counselor counselor);
}
