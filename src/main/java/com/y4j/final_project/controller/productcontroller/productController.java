package com.y4j.final_project.controller.productcontroller;

import java.io.Console;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysql.cj.Session;
import com.y4j.final_project.command.OrderHistoryVO;
import com.y4j.final_project.command.UserOrderVO;
import com.y4j.final_project.command.UserVO;
import com.y4j.final_project.command.ordercommand.ProductVO;
import com.y4j.final_project.product.service.ProductService;
import com.y4j.final_project.util.Criteria;
import com.y4j.final_project.util.MoreVO;
//import com.y4j.final_project.util.PageVO;
import com.y4j.final_project.util.PageVO;


@Controller
@RequestMapping("/product")
public class productController {
	
	@Autowired
	@Qualifier("productService")
	private ProductService productService;
	
	
	//유저 레이아웃
	@GetMapping("/user_layout")
	public String user_layout () {
		return "layout/user_layout";
	}
	
	//상품 판매 페이지 기능 구현 (1) - 상품 목록 가져오기
	@GetMapping("/productList")
	public String productList (Model model,Criteria cri, ProductVO vo, MoreVO vo1) {
		
		//All about part
		ArrayList<ProductVO> list = productService.productList(cri, vo1);
		model.addAttribute("list", list);	
		
		model.addAttribute("cri", cri);
		
		return "product/product_page";
		
	}
	
	//상품 상세 페이지 형식
	@GetMapping("/detail_page")
	public String productDetail(@RequestParam("prod_no") int prod_no,  Model model) {
		
		ArrayList<ProductVO> detail =  productService.productDetailList(prod_no);
		model.addAttribute("detail", detail);
		return "product/productDetail_page";
	}
	
	//구매 페이지
	@PostMapping("/buy_page")
	public String userOrderRightNow(Model model, UserOrderVO vo1, HttpSession session, UserVO vo) {
		
		//세션에 로그인 아이디 정보가 있다고 할 때,
		//회원이라고 가정할 때 구매와 장바구니 담기 가능 
		//session.setAttribute("user_id", "");
		//String user_id=(String)session.getAttribute("user_id");
		
		//일반 회원이 아니라면
		//if(!user_id.equals("")) {
			//return "redirect:/user/user_login";
		//}
		
		
		//내가 가상으로 로그인 했다면 (해결완)
		String user_id = "gmlwjd852369";
		if(!user_id.equals(vo1.getUser_id())) {
			return "redirect:/user/user_login";
		}

		ArrayList<UserVO> user= productService.userOrderRightNow(vo);
		model.addAttribute("user", user);
		
		//데이터베이스까지 안 가고 직전에 바로 꺼내오기 
		//배송비 처리(5만원 이상 무료 배송)
		int total =  vo1.getUser_order_total_price();
		if( total < 50000) {
			vo1.setUser_delivery_fee("3,000원");
		}else {
			vo1.setUser_delivery_fee("무료");
		}
		
		model.addAttribute("user_order", vo1);
		System.out.println(vo1.toString());
		return "product/product_buy_rightnow";
	}
	
	//주문 폼(페이지)
	@PostMapping("/orderform")
	public String user_order(OrderHistoryVO vo, RedirectAttributes ra) {
	
		
		int new_order_total = vo.getOrder_total_price();
		if( new_order_total < 50000) {
			new_order_total += 3000;
			vo.setOrder_total_price(new_order_total);
		}
		
		
		int result = productService.user_order(vo);
		String msg = result == 1 ? "결제가 정상적으로 완료되었습니다" : "결제에 실패하였습니다";
		ra.addFlashAttribute("msg", msg);
		return "redirect:/product/user_orderList";
	}
	
	//주문내역 페이지
	@GetMapping("/user_orderList")
	public String user_orderList( Model model, OrderHistoryVO vo, Criteria cri) {
		ArrayList<OrderHistoryVO> user_orderList =  productService.user_orderList(vo, cri);
		System.out.println(cri.getSearchName());

		model.addAttribute("cri", cri);
		model.addAttribute("user_orderList", user_orderList);
		
		//페이지네이션 처리
		int total = productService.getProdOrderTotal(cri);
		PageVO pageVO = new PageVO(cri, total);
		model.addAttribute("pageVO", pageVO);
		
		return"user/order_list";
	}
	
	//주문 내역 상세페이지
	@GetMapping("/user_order_details")
	public String user_order_details (Model model, @RequestParam("order_prod_no")String order_prod_no) {
		
		ArrayList<OrderHistoryVO> user_order_details = productService.user_orderList_detail(order_prod_no);
		 model.addAttribute("user_order_details", user_order_details);
		System.out.println(user_order_details);
		
		return "user/order_details";
	}
	
	//---------------------- 연예인 별 상품 페이지 - 배우 -----------------------//
	// 채수빈 페이지
	@GetMapping("/CHAESOOBIN")
	public String soobin(Model model,Criteria cri) {
		
		ArrayList<ProductVO> soobin_list =  productService.productList_soobin(cri);
		model.addAttribute("soobin_list", soobin_list);
	
		
		return "product/productDetail_chaesoobin_page";
	}
	
	//이도현 페이지
	@GetMapping("/LEEDOHYUN")
	public String dohyun(Model model, Criteria cri) {
		
		ArrayList<ProductVO> dohyun_list =  productService.productList_soobin(cri);
		model.addAttribute("dohyun_list", dohyun_list);
	
		
		return "product/productDetail_leedohyun_page";
	}
	
	
	//------------------------- 연예인 별 상품 페이지 - 가수 -------------------------//
	
	//블랙핑크 페이지
	@GetMapping("/BLACKPINK")
	public String blackpink(Model model, Criteria cri) {
		
		ArrayList<ProductVO> blackpink_list =  productService.productList_blackpink(cri);
		model.addAttribute("blackpink_list", blackpink_list);
		
		
		return "product/productDetail_blackpink_page";
	}
	
	//아이브 페이지
	@GetMapping("/IVE")
	public String ive(Model model, Criteria cri) {
		
		ArrayList<ProductVO> ive_list =  productService.productList_ive(cri);
		model.addAttribute("ive_list", ive_list);
	
		
		return "product/productDetail_ive_page";
	}
	
	//뉴진스 페이지
	@GetMapping("/NEWJEANS")
	public String newjeans(Model model, Criteria cri) {
		
		ArrayList<ProductVO> newjeans_list =  productService.productList_newjeans(cri);
		model.addAttribute("newjeans_list", newjeans_list);

		
		return "product/productDetail_newjeans_page";
	}
	
}