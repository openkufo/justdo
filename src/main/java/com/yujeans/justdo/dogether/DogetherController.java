package com.yujeans.justdo.dogether;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yujeans.justdo.category.Category;
import com.yujeans.justdo.user.Account;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/regist")
@RequiredArgsConstructor
public class DogetherController {
	
	private final DogetherService dogetherService;
	
	@GetMapping
	public String registForm(Model model) {
		
		model.addAttribute("dogether", new Dogether());
		
		return "dogether/dogetherRegist";
	}
	
	@PostMapping
	public String saveDogether(Dogether dogetherForm,
								Model model) {
		
		Dogether dogether = new Dogether();
		dogether.setTitle(dogetherForm.getTitle());
		dogether.setImage(dogetherForm.getImage());
		dogether.setLeaderInfo(dogetherForm.getLeaderInfo());
		dogether.setSummary(dogetherForm.getSummary());
		dogether.setRecommendTo(dogetherForm.getRecommendTo());
		dogether.setNotice(dogetherForm.getRecommendTo());
		
//		dogether.getCategory().setId(dogetherForm.getCategory().getId());
		
		dogetherService.saveDogether(dogether);
		
		return "redirect:/{dogetherId}/detail(dogetherId=${dogether.id})";
	}
	
	
	@GetMapping("/{dogetherId}/detail")
	public String dogetherDetail(@PathVariable("dogetherId")Long dogetherId, Model model){
		
		Dogether findDogether = dogetherService.findDogether(dogetherId);
		model.addAttribute("findDogether", findDogether);
		
		//AccountDogether 테이블 select *
		AccountDogether userId = dogetherService.findAccountDogether(dogetherId);
		
		Account findAccount = dogetherService.findAccount(userId.getId());
		model.addAttribute("findAccount", findAccount); // 두리더 사진 넣기 위함
		
		
		return "dogether/classDetail";
	}
	
	
	

}