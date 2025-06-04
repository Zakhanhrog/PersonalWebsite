<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />
<!-- Page Banner -->
<section class="page-banner">
    <div class="container">
        <div class="page-banner-content text-center">
            <h1 data-aos="fade-up">Về Tôi</h1>
        </div>
    </div>
</section>
<!-- About Detail Section -->
<section class="about-detail-area section-padding">
    <div class="container">
        <c:if test="${not empty profile}">
            <div class="section-title text-center" data-aos="fade-up">
                <h2>Hồ Sơ Cá Nhân</h2>
            </div>
            <div class="row align-items-center">
                <div class="col-lg-5" data-aos="fade-right" data-aos-delay="100">
                    <div class="about-detail-img">
                            <%-- SỬA Ở ĐÂY --%>
                        <img src="${not empty profile.photoUrl ? profile.photoUrl : pageContext.request.contextPath += '/resources/images/default-profile-full.jpg'}"
                             alt="Ảnh của ${profile.name}"
                             onerror="this.src='${pageContext.request.contextPath}/resources/images/default-profile-full-placeholder.jpg'; this.onerror=null;">
                    </div>
                </div>
                <div class="col-lg-7" data-aos="fade-left" data-aos-delay="200">
                    <div class="about-detail-content">
                        <h3>
                            <c:out value="${profile.name}"/>
                        </h3>
                        <h4>
                            <c:out value="${profile.position}"/>
                            <c:if test="${not empty profile.companyName}">
                                -
                                <c:out value="${profile.companyName}"/>
                            </c:if>
                        </h4>
                        <p class="text-muted" style="white-space: pre-line;">
                            <c:out value="${profile.bio}"/>
                        </p>
                        <p>Với phương châm "Thành công là một hành trình, không phải đích đến", tôi luôn tìm kiếm những cơ hội mới để phát triển bản thân và doanh nghiệp. Tôi tin rằng sự kết hợp giữa tầm nhìn chiến lược, sự chuyên nghiệp và tinh thần đổi mới sẽ tạo nên những giá trị bền vững cho công ty, đối tác và cộng đồng.</p>
                        <div class="about-info-list mt-4">
                            <div class="info-item">
                                <i class="fas fa-id-badge text-primary"></i>
                                <div>
                                    <strong>Họ tên:</strong>
                                    <c:out value="${profile.name}"/>
                                </div>
                            </div>
                            <div class="info-item">
                                <i class="fas fa-briefcase text-primary"></i>
                                <div>
                                    <strong>Chức vụ:</strong>
                                    <c:out value="${profile.position}"/>
                                </div>
                            </div>
                            <c:if test="${not empty profile.companyName}">
                                <div class="info-item">
                                    <i class="fas fa-building text-primary"></i>
                                    <div>
                                        <strong>Công ty:</strong>
                                        <c:out value="${profile.companyName}"/>
                                    </div>
                                </div>
                            </c:if>
                            <div class="info-item">
                                <i class="fas fa-envelope text-primary"></i>
                                <div>
                                    <strong>Email:</strong>
                                    <a href="mailto:${profile.email}">
                                        <c:out value="${profile.email}"/>
                                    </a>
                                </div>
                            </div>
                            <div class="info-item">
                                <i class="fas fa-phone text-primary"></i>
                                <div>
                                    <strong>Điện thoại:</strong>
                                    <a href="tel:${profile.phoneNumber}">
                                        <c:out value="${profile.phoneNumber}"/>
                                    </a>
                                </div>
                            </div>
                            <c:if test="${not empty profile.companyAddress}">
                                <div class="info-item">
                                    <i class="fas fa-map-marker-alt text-primary"></i>
                                    <div>
                                        <strong>Địa chỉ:</strong>
                                        <c:out value="${profile.companyAddress}"/>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${empty profile}">
            <p class="text-center">Thông tin hồ sơ hiện chưa có sẵn.</p>
        </c:if>
    </div>
</section>
<c:if test="${not empty skillsList}">
    <section class="skills-area section-padding bg-light" id="skills">
        <div class="container">
            <div class="section-title text-center" data-aos="fade-up">
                <h2>Kỹ Năng & Chuyên Môn</h2>
            </div>
            <div class="row">
                <c:forEach items="${skillsList}" var="skill" varStatus="loop">
                    <div class="col-lg-6" data-aos="fade-up" data-aos-delay="${100 * (loop.index % 2)}">
                        <div class="skill-bar">
                            <div class="skill-info">
                        <span class="skill-name">
                           <c:out value="${skill.name}"/>
                           <c:if test="${not empty skill.category}">
                               (
                               <c:out value="${skill.category}"/>
                               )
                           </c:if>
                        </span>
                                <span class="skill-level-text">${skill.level}%</span>
                            </div>
                            <div class="skill-progress">
                                <div class="skill-progress-bar" style="width: 0%;" data-progress="${skill.level}%"></div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </section>
</c:if>
<section class="timeline-section section-padding" id="experience">
    <div class="container">
        <div class="section-title text-center" data-aos="fade-up">
            <h2>Học Vấn & Kinh Nghiệm</h2>
        </div>
        <div class="row">
            <div class="col-lg-6" data-aos="fade-right" data-aos-delay="100">
                <h3 class="timeline-group-title"><i class="fas fa-graduation-cap text-primary me-2"></i> Học Vấn</h3>
                <div class="timeline">
                    <c:choose>
                        <c:when test="${not empty educationsList}">
                            <c:forEach items="${educationsList}" var="edu">
                                <div class="timeline-item">
                                    <div class="timeline-content">
                              <span class="timeline-date">
                                 <c:out value="${edu.startYear}"/>
                                 -
                                 <c:out value="${edu.endYear ne null ? edu.endYear : 'Hiện tại'}"/>
                              </span>
                                        <h4 class="timeline-title">
                                            <c:out value="${edu.degree}"/>
                                        </h4>
                                        <p class="timeline-desc">
                                            <strong>
                                                <c:out value="${edu.schoolName}"/>
                                            </strong>
                                        </p>
                                        <c:if test="${not empty edu.fieldOfStudy}">
                                            <p class="timeline-desc">
                                                Chuyên ngành:
                                                <c:out value="${edu.fieldOfStudy}"/>
                                            </p>
                                        </c:if>
                                        <c:if test="${not empty edu.description}">
                                            <p class="timeline-desc text-muted">
                                                <small>
                                                    <c:out value="${edu.description}"/>
                                                </small>
                                            </p>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p>Chưa có thông tin học vấn.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="col-lg-6" data-aos="fade-left" data-aos-delay="200">
                <h3 class="timeline-group-title"><i class="fas fa-briefcase text-primary me-2"></i> Kinh Nghiệm</h3>
                <div class="timeline">
                    <c:choose>
                        <c:when test="${not empty experiencesList}">
                            <c:forEach items="${experiencesList}" var="exp">
                                <div class="timeline-item">
                                    <div class="timeline-content">
                              <span class="timeline-date">
                                 <fmt:formatDate value="${exp.startDate}" pattern="MM/yyyy"/>
                                 -
                                 <c:choose>
                                     <c:when test="${not empty exp.endDate}">
                                         <fmt:formatDate value="${exp.endDate}" pattern="MM/yyyy"/>
                                     </c:when>
                                     <c:otherwise>Hiện tại</c:otherwise>
                                 </c:choose>
                              </span>
                                        <h4 class="timeline-title">
                                            <c:out value="${exp.position}"/>
                                        </h4>
                                        <p class="timeline-desc">
                                            <strong>
                                                <c:out value="${exp.companyName}"/>
                                            </strong>
                                        </p>
                                        <c:if test="${not empty exp.descriptionResponsibilities}">
                                            <p class="timeline-desc text-muted" style="white-space: pre-line;">
                                                <small>
                                                    <c:out value="${exp.descriptionResponsibilities}"/>
                                                </small>
                                            </p>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p>Chưa có thông tin kinh nghiệm.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</section>
<c:if test="${not empty testimonialsList}">
    <section class="testimonials-area section-padding bg-light" id="testimonials">
        <div class="container">
            <div class="section-title text-center" data-aos="fade-up">
                <h2>Đánh Giá Từ Đối Tác</h2>
            </div>
            <div class="row">
                <c:forEach items="${testimonialsList}" var="testimonial" varStatus="loop">
                    <div class="col-lg-4 col-md-6" data-aos="fade-up" data-aos-delay="${100 * loop.index}">
                        <div class="testimonial-item">
                            <div class="client-img">
                                    <%-- SỬA Ở ĐÂY --%>
                                <img src="${not empty testimonial.clientImageUrl ? testimonial.clientImageUrl : 'https://ui-avatars.com/api/?name=' += fn:escapeXml(fn:replace(testimonial.clientName, ' ', '+')) += '&size=100&background=random'}"
                                     alt="Ảnh của ${testimonial.clientName}"
                                     onerror="this.src='https://ui-avatars.com/api/?name=${fn:escapeXml(fn:replace(testimonial.clientName, ' ', '+'))}&size=100&background=random&color=fff'; this.onerror=null;">
                            </div>
                            <div class="testimonial-text">
                                <i class="fas fa-quote-left"></i>
                                <p>
                                    <c:out value="${testimonial.quoteText}"/>
                                </p>
                            </div>
                            <div class="client-info">
                                <h4>
                                    <c:out value="${testimonial.clientName}"/>
                                </h4>
                                <p>
                                    <c:out value="${testimonial.clientPositionCompany}"/>
                                </p>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </section>
</c:if>
<jsp:include page="/WEB-INF/includes/footer.jsp" />