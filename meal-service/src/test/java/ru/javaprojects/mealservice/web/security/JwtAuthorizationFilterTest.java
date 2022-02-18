package ru.javaprojects.mealservice.web.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.mealservice.MealMatcher;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.util.exception.ErrorType;
import ru.javaprojects.mealservice.web.json.JsonUtil;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javaprojects.mealservice.testdata.MealTestData.*;
import static ru.javaprojects.mealservice.testdata.UserTestData.USER1_ID_STRING;
import static ru.javaprojects.mealservice.util.exception.ErrorType.BAD_TOKEN_ERROR;
import static ru.javaprojects.mealservice.web.AppExceptionHandler.EXCEPTION_BAD_TOKEN;
import static ru.javaprojects.mealservice.web.security.JwtProvider.AUTHORITIES;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class JwtAuthorizationFilterTest {
    private static final String REST_URL = "/api/meals/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    private HttpHeaders jwtHeader;
    private HttpHeaders jwtExpiredHeader;
    private HttpHeaders jwtInvalidHeader;

    public ResultMatcher errorType(ErrorType type) {
        return jsonPath("$.type").value(type.name());
    }

    public ResultMatcher detailMessage(String code) {
        return jsonPath("$.details").value(code);
    }

    @PostConstruct
    private void postConstruct() {
        String secretKey = environment.getProperty("jwt.secretKey");
        jwtHeader = getHeaders(generateCustomAuthorizationToken(USER1_ID_STRING,
                new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("authorization.token.expiration-time"))),
                secretKey, "ROLE_USER"));
        jwtExpiredHeader = getHeaders(generateCustomAuthorizationToken(USER1_ID_STRING,
                new Date(System.currentTimeMillis() - 10000), secretKey, "ROLE_USER"));
        jwtInvalidHeader = getHeaders(generateCustomAuthorizationToken(USER1_ID_STRING,
                new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("authorization.token.expiration-time"))),
                UUID.randomUUID().toString(), "ROLE_USER"));
    }

    @Test
    void getPage() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(PAGE_NUMBER_PARAM, PAGE_NUMBER)
                .param(PAGE_SIZE_PARAM, PAGE_SIZE)
                .headers(jwtHeader))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        List<Meal> meals = JsonUtil.readContentFromPage(actions.andReturn().getResponse().getContentAsString(), Meal.class);
        MealMatcher.assertMatch(meals, meal7, meal6, meal5, meal4, meal3);
    }

    @Test
    void getPageTokenExpired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(PAGE_NUMBER_PARAM, PAGE_NUMBER)
                .param(PAGE_SIZE_PARAM, PAGE_SIZE)
                .headers(jwtExpiredHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(BAD_TOKEN_ERROR))
                .andExpect(detailMessage(EXCEPTION_BAD_TOKEN));
    }

    @Test
    void getPageTokenInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(PAGE_NUMBER_PARAM, PAGE_NUMBER)
                .param(PAGE_SIZE_PARAM, PAGE_SIZE)
                .headers(jwtInvalidHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(BAD_TOKEN_ERROR))
                .andExpect(detailMessage(EXCEPTION_BAD_TOKEN));
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, environment.getProperty("authorization.token.header.prefix") + token);
        return httpHeaders;
    }

    private String generateCustomAuthorizationToken(String userId, Date expirationDate, String secretKey, String ... authorities) {
        return JWT.create()
                .withIssuer(environment.getProperty("authorization.token.issuer"))
                .withAudience(environment.getProperty("authorization.token.audience"))
                .withIssuedAt(new Date())
                .withSubject(userId)
                .withArrayClaim(AUTHORITIES, authorities)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC512(secretKey));
    }
}