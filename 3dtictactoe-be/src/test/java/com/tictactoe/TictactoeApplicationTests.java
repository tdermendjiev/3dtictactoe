package com.tictactoe;

import com.tictactoe.controller.MoveController;
import com.tictactoe.repository.JdbcBoardStateRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;
import static  org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(SpringRunner.class)
@SpringBootTest
public class TictactoeApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private MoveController controller;

	@Autowired
	private JdbcBoardStateRepository repository;

//	@Before
//	public void setup() {
//		this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//	}
//
//	@Test
//	void contextLoads() {
//		assertThat(controller).isNotNull();
//	}
//
//	@Test
//	public void getNewBoard()
//			throws Exception {
//
//
//		this.mvc.perform(get("/api/board")).andExpect(status().isOk())
//				.andExpect(content().contentType("application/json;charset=UTF-8"));
//	}

}
