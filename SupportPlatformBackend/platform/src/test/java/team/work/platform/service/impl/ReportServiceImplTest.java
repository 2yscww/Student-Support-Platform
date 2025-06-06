package team.work.platform.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import team.work.platform.common.Response;
import team.work.platform.common.JwtAuthenticationFilter;
import team.work.platform.dto.ReportSubmitDTO;
import team.work.platform.mapper.ReportsMapper;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.mapper.TaskMapper;
import team.work.platform.mapper.ReviewMapper;
import team.work.platform.model.Reports;
import team.work.platform.model.Tasks;
import team.work.platform.model.Reviews;
import team.work.platform.model.enumValue.ReportStatus;
import team.work.platform.model.enumValue.ReportType;
import team.work.platform.utils.UserValidator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {

    @Mock
    private ReportsMapper reportsMapper;

    @Mock
    private UsersMapper usersMapper;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private ReportServiceImpl reportService;

    private ReportSubmitDTO reportSubmitDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportSubmitDTO = new ReportSubmitDTO();
        // 设置基本的测试数据
        reportSubmitDTO.setReason("测试举报原因");
    }

    @Test
    void submitReport_WhenUserNotLoggedIn_ShouldReturnFail() {
        // 模拟用户未登录情况
        try (MockedStatic<JwtAuthenticationFilter> mockedStatic = mockStatic(JwtAuthenticationFilter.class)) {
            mockedStatic.when(JwtAuthenticationFilter::getCurrentUserId).thenReturn(null);
            
            Response<Object> response = reportService.submitReport(reportSubmitDTO);
            
            assertNotNull(response);
            assertEquals(400, response.getCode());
            assertEquals("用户未登录!", response.getMsg());
        }
    }

    @Test
    void submitReport_WhenReasonIsEmpty_ShouldReturnFail() {
        // 模拟用户已登录
        try (MockedStatic<JwtAuthenticationFilter> mockedStatic = mockStatic(JwtAuthenticationFilter.class)) {
            mockedStatic.when(JwtAuthenticationFilter::getCurrentUserId).thenReturn(1L);
            
            reportSubmitDTO.setReason("");
            
            Response<Object> response = reportService.submitReport(reportSubmitDTO);
            
            assertNotNull(response);
            assertEquals(400, response.getCode());
            assertEquals("举报原因不能为空!", response.getMsg());
        }
    }

    @Test
    void submitReport_WhenReportingUser_WithValidData_ShouldSucceed() {
        // 模拟用户已登录
        try (MockedStatic<JwtAuthenticationFilter> mockedStatic = mockStatic(JwtAuthenticationFilter.class)) {
            mockedStatic.when(JwtAuthenticationFilter::getCurrentUserId).thenReturn(1L);
            
            reportSubmitDTO.setReportType(ReportType.USER);
            reportSubmitDTO.setReportedUserId(2L);
            when(userValidator.isUserIDExist(2L)).thenReturn(true);
            
            Response<Object> response = reportService.submitReport(reportSubmitDTO);
            
            assertNotNull(response);
            assertEquals(200, response.getCode());
            assertEquals("举报提交成功!", response.getMsg());
            verify(reportsMapper).createReport(any(Reports.class));
        }
    }

    @Test
    void submitReport_WhenReportingTask_WithValidData_ShouldSucceed() {
        // 模拟用户已登录
        try (MockedStatic<JwtAuthenticationFilter> mockedStatic = mockStatic(JwtAuthenticationFilter.class)) {
            mockedStatic.when(JwtAuthenticationFilter::getCurrentUserId).thenReturn(1L);
            
            reportSubmitDTO.setReportType(ReportType.TASK);
            reportSubmitDTO.setReportedOrderId(1L);
            when(taskMapper.selectByTaskId(1L)).thenReturn(new Tasks()); // 模拟任务存在
            
            Response<Object> response = reportService.submitReport(reportSubmitDTO);
            
            assertNotNull(response);
            assertEquals(200, response.getCode());
            assertEquals("举报提交成功!", response.getMsg());
            verify(reportsMapper).createReport(any(Reports.class));
        }
    }

    @Test
    void submitReport_WhenReportingReview_WithValidData_ShouldSucceed() {
        // 模拟用户已登录
        try (MockedStatic<JwtAuthenticationFilter> mockedStatic = mockStatic(JwtAuthenticationFilter.class)) {
            mockedStatic.when(JwtAuthenticationFilter::getCurrentUserId).thenReturn(1L);
            
            reportSubmitDTO.setReportType(ReportType.REVIEW);
            reportSubmitDTO.setReportedReviewId(1L);
            when(reviewMapper.selectByReviewId(1L)).thenReturn(new Reviews()); // 模拟评价存在
            
            Response<Object> response = reportService.submitReport(reportSubmitDTO);
            
            assertNotNull(response);
            assertEquals(200, response.getCode());
            assertEquals("举报提交成功!", response.getMsg());
            verify(reportsMapper).createReport(any(Reports.class));
        }
    }

    @Test
    void submitReport_WhenReportingSelf_ShouldReturnFail() {
        // 模拟用户已登录
        try (MockedStatic<JwtAuthenticationFilter> mockedStatic = mockStatic(JwtAuthenticationFilter.class)) {
            mockedStatic.when(JwtAuthenticationFilter::getCurrentUserId).thenReturn(1L);
            
            reportSubmitDTO.setReportType(ReportType.USER);
            reportSubmitDTO.setReportedUserId(1L);
            // 模拟用户ID存在
            when(userValidator.isUserIDExist(1L)).thenReturn(true);
            
            Response<Object> response = reportService.submitReport(reportSubmitDTO);
            
            assertNotNull(response);
            assertEquals(400, response.getCode());
            assertEquals("不能举报自己!", response.getMsg());
        }
    }
} 