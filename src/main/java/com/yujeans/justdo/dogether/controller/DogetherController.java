package com.yujeans.justdo.dogether.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yujeans.justdo.category.Category;
import com.yujeans.justdo.category.FirstCategory;
import com.yujeans.justdo.category.SecondCategory;
import com.yujeans.justdo.category.ThirdCategory;
import com.yujeans.justdo.category.service.CategoryService;
import com.yujeans.justdo.dogether.AccountDogether;
import com.yujeans.justdo.dogether.Dogether;
import com.yujeans.justdo.dogether.DogetherRegistDTO;
import com.yujeans.justdo.dogether.DogetherService;
import com.yujeans.justdo.user.Account;

import dto.CategoryDto;
import lombok.RequiredArgsConstructor;


@Controller
//@RequestMapping("/regist")
@RequiredArgsConstructor
public class DogetherController {
	
	@Autowired
	private final DogetherService dogetherService;
	private final CategoryService categoryService;
	
	//메인 페이지에서 '두게더 등록' 클릭 시 두게더 등록 페이지로 이동
	@GetMapping("/dogether/registForm")
	   public String dogetherRegistForm(HttpServletRequest request, Model model) {
	      System.out.println("nickname : "+request.getAttribute("nickname"));
	      System.out.println("id : "+request.getAttribute("id"));
	     
	      // 대분류 조회 후 전달
	      List<FirstCategory> firstCategory = dogetherService.selectFirstCategory();
	      model.addAttribute("firstCategory", firstCategory);
	      
	      // 빈 객체 전달
	      Dogether dogether = new Dogether();
	      model.addAttribute("dogether", dogether);
	      
	      return "dogether/dogether_regist";
	   }

	 @GetMapping("/dogether/listForm")
	   public String dogetherListForm() {
	      return "dogether/dogether_list";
	  }

	//두번째 카테고리 가져오기(ajax)
	@ResponseBody
	@PostMapping("/firstcategory")
	public List<SecondCategory> secondCategory(@RequestBody CategoryDto selectedData, Model model) {
		
//		System.out.println("selectFirst::" + selectedData.getFirstData());
		
		//선택된 대분류 값 넘겨서 중분류 가져오기
		List<SecondCategory> secondCategoryList = categoryService.findSecondCategory(selectedData.getFirstData());

		model.addAttribute(secondCategoryList);
		

		return secondCategoryList;
	}
	
	//세번째 카테고리 가져오기(ajax)
		@ResponseBody
		@PostMapping("/secondcategory")
		public List<ThirdCategory> thirdCategory(@RequestBody CategoryDto selectedData, Model model) {
			
//			System.out.println("selectSecond::" + selectedData.getFirstData());
			
			//선택된 중분류 값 넘겨서 소분류 가져오기
			List<ThirdCategory> thirdCategoryList = categoryService.findThirdCategory(selectedData.getFirstData());

			model.addAttribute(thirdCategoryList);

			return thirdCategoryList;
		}

	// 작성된 두게더(클래스) 저장
	@PostMapping("/regist/dogether")
	public String saveDogether(@ModelAttribute DogetherRegistDTO dogetherForm, Model model, HttpServletRequest request) {//@RequestParam("thirdCateSelect") String thirdCateSelect,
		
		String selectedThird = dogetherForm.getThirdCateSelect();

		//---------------테스트---------------------
		System.out.println("닉네임 출력해보기 : " + request.getAttribute("nickname"));
		System.out.println("아이디 출력해보기 : " + request.getAttribute("id"));

		System.out.println("dto에서 가져온 세번째 카테고리 value 값 ::" + dogetherForm.getThirdCateSelect());
		System.out.println("두게더 제목 : " + dogetherForm.getTitle());
		
//		System.out.println("requestParam에서 가져온 값 ::" + thirdCateSelect);
		//---------------테스트---------------------
		
		// 카테고리 아아디 가져오기 & Category 클래스의 id set
		Long categoryId = categoryService.findCategoryId(selectedThird);
		Category cate = new Category();
		cate.setId(categoryId);
		
		// id 세팅
		Long accountId = Long.parseLong(String.valueOf(request.getAttribute("id"))); 
		//request.getAttribute("id") : object타입
		// -> String으로 형변환 -> Long으로 형변환
		Account account = new Account();
		account.setId(accountId);
		
		Dogether dogether = new Dogether();
		dogether.setTitle(dogetherForm.getTitle()); 			// 제목
		dogether.setImage(dogetherForm.getImage());				// 두리더 이미지
		System.out.println("이미지 경로 :: "+dogetherForm.getImage());
		dogether.setLeaderInfo(dogetherForm.getLeaderInfo());	// 두리더 정보
		dogether.setSummary(dogetherForm.getSummary());			// 요약
		dogether.setRecommendTo(dogetherForm.getRecommendTo());	// 추천 대상
		dogether.setDetail(dogetherForm.getDetail());			// 상세설명
		dogether.setNotice(dogetherForm.getNotice());			// 공지사항
		dogether.setPrice(dogetherForm.getPrice());				// 가격
		dogether.setCategory(cate);								// 카테고리 id
		dogether.setAccount(account);
		
		System.out.println("카테고리 설정한 id값 확인 ~~~ :: " + dogether.getCategory().getId());
		// 없는 부분 : accountId
		
		
		dogetherService.saveDogether(dogether);
		
		return "redirect:/{dogetherId}/detail(dogetherId=${dogether.id})";
	}
	
	
	@GetMapping("/{dogetherId}/detail")
	public String dogetherDetail(@RequestParam("dogetherId")Long dogetherId, Model model){
		
		Dogether findDogether = dogetherService.findDogether(dogetherId);
		model.addAttribute("findDogether", findDogether);
		
		//AccountDogether 테이블 select *
		AccountDogether userId = dogetherService.findAccountDogether(dogetherId);
		
		Account findAccount = dogetherService.findAccount(userId.getId());
		model.addAttribute("findAccount", findAccount); // 두리더 사진 넣기 위함
		
		
		return "dogether/classDetail";
	}
	

}