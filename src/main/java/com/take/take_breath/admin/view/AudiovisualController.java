package com.take.take_breath.admin.view;

import com.take.take_breath.audiovisualmaterial.AudiovisualService;
import com.take.take_breath.audiovisualmaterial.dto.AudiovisualRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/admin/view/audiovisual")
@RequiredArgsConstructor
public class AudiovisualController {

    private final AudiovisualService audiovisualService;

    // 리스트 페이지
    @GetMapping
    public String list(Model model) {
        model.addAttribute("videos", audiovisualService.findAll());
        model.addAttribute("pageTitle", "시청각 자료실 관리");
        model.addAttribute("isAudiovisual", true);
        return "admin/audiovisual-list";
    }

    // 등록 페이지
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("pageTitle", "시청각 자료 등록");
        model.addAttribute("isAudiovisual", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-audiovisual.css"});
        return "admin/audiovisual-register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute AudiovisualRequest dto) {
        audiovisualService.save(dto);
        return "redirect:/api/admin/view/audiovisual"; // ✅ 수정
    }

    // 수정 페이지
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("video", audiovisualService.findById(id));
        model.addAttribute("pageTitle", "시청각 자료 수정");
        model.addAttribute("isAudiovisual", true);
        model.addAttribute("additionalCss", new String[]{"/css/admin-audiovisual.css"});
        return "admin/audiovisual-edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute AudiovisualRequest dto) {
        audiovisualService.update(id, dto);
        return "redirect:/api/admin/view/audiovisual";
    }

    // 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        audiovisualService.delete(id);
        return "redirect:/api/admin/view/audiovisual";
    }
}
