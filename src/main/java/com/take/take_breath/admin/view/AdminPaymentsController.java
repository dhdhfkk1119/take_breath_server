package com.take.take_breath.admin.view;

import com.take.take_breath._core.auth.Auth;
import com.take.take_breath.members.Role;
import com.take.take_breath.members.Status;
import com.take.take_breath.payment.PaymentResponse;
import com.take.take_breath.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/view/payments")
public class AdminPaymentsController {

    private final PaymentService paymentService;

    @GetMapping("/stats")
    public String getPaymentStats(Model model) {
        PaymentResponse.AdminFeeStatsDTO totalStats = paymentService.getAdminFeeStats();

        List<PaymentResponse.AdminFeeStatsDTO> monthlyStats = paymentService.getMonthlyFeeStats();

        model.addAttribute("pageTitle", "결제 통계 관리");
        model.addAttribute("totalStats", totalStats);
        model.addAttribute("monthlyStats", monthlyStats);
        model.addAttribute("isPayments", true); // 사이드바 활성화용 플래그
        model.addAttribute("additionalCss", new String[]{"/css/admin-common.css", "/css/admin-payments.css"});
        model.addAttribute("additionalScript", new String[]{"/js/admin-payments-stats.js"});

        return "admin/payments-stats";
    }

}
