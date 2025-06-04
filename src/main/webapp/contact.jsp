<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/includes/header.jsp" />

<!-- Page Banner -->
<section class="page-banner">
    <div class="container">
        <div class="page-banner-content text-center">
            <h1 data-aos="fade-up">Liên Hệ</h1>
        </div>
    </div>
</section>

<!-- Contact Info Section -->
<section class="contact-info-area section-padding">
    <div class="container">
        <div class="row">
            <div class="col-lg-4 col-md-6" data-aos="fade-up" data-aos-delay="100">
                <div class="contact-info-box">
                    <div class="contact-icon"><i class="fas fa-map-marker-alt"></i></div>
                    <h3 class="contact-title">Địa Chỉ Văn Phòng</h3>
                    <p><c:out value="${profile.companyAddress ne null ? profile.companyAddress : 'Vui lòng cập nhật địa chỉ.'}"/></p>
                </div>
            </div>

            <div class="col-lg-4 col-md-6" data-aos="fade-up" data-aos-delay="200">
                <div class="contact-info-box">
                    <div class="contact-icon"><i class="fas fa-phone-alt"></i></div>
                    <h3 class="contact-title">Điện Thoại</h3>
                    <p>
                        <c:if test="${not empty profile.phoneNumber}">
                            Di động: <a href="tel:${profile.phoneNumber}"><c:out value="${profile.phoneNumber}"/></a><br>
                        </c:if>
                        <%-- Add office phone if available in profile or static --%>
                        Văn phòng: <a href="tel:+842438888888">+84 243 888 8888</a> (Ví dụ)
                    </p>
                </div>
            </div>

            <div class="col-lg-4 col-md-12" data-aos="fade-up" data-aos-delay="300"> <%-- col-md-12 for full width on medium if only 3 items --%>
                <div class="contact-info-box">
                    <div class="contact-icon"><i class="fas fa-envelope"></i></div>
                    <h3 class="contact-title">Địa Chỉ Email</h3>
                    <p>
                        <c:if test="${not empty profile.email}">
                            Cá nhân: <a href="mailto:${profile.email}"><c:out value="${profile.email}"/></a><br>
                        </c:if>
                        <%-- Add company email if available in profile or static --%>
                        Công ty: <a href="mailto:info@gmtkinhbac.com">info@gmtkinhbac.com</a> (Ví dụ)
                    </p>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Contact Form & Map Section -->
<section class="contact-form-map-area section-padding bg-light">
    <div class="container">
        <div class="row">
            <div class="col-lg-6" data-aos="fade-right">
                <div class="contact-form-wrapper">
                    <div class="section-title text-left">
                        <h2>Gửi Tin Nhắn Cho Chúng Tôi</h2>
                        <p>Nếu bạn có bất kỳ câu hỏi hoặc yêu cầu nào, vui lòng điền vào biểu mẫu bên dưới.</p>
                    </div>

                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <c:out value="${successMessage}"/>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <c:out value="${errorMessage}"/>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/contact" method="post" class="contact-form">
                        <div class="row">
                            <div class="col-md-6 form-group">
                                <label for="contactName" class="form-label">Họ tên *</label>
                                <input type="text" id="contactName" name="name" class="form-control" value="<c:out value='${formName}'/>" required>
                            </div>
                            <div class="col-md-6 form-group">
                                <label for="contactEmail" class="form-label">Email *</label>
                                <input type="email" id="contactEmail" name="email" class="form-control" value="<c:out value='${formEmail}'/>" required>
                            </div>
                            <div class="col-md-6 form-group">
                                <label for="contactPhone" class="form-label">Điện thoại</label>
                                <input type="tel" id="contactPhone" name="phone" class="form-control" value="<c:out value='${formPhone}'/>">
                            </div>
                            <div class="col-md-6 form-group">
                                <label for="contactSubject" class="form-label">Chủ đề *</label>
                                <input type="text" id="contactSubject" name="subject" class="form-control" value="<c:out value='${formSubject}'/>" required>
                            </div>
                            <div class="col-12 form-group">
                                <label for="contactMessage" class="form-label">Nội dung tin nhắn *</label>
                                <textarea id="contactMessage" name="message" rows="5" class="form-control" required><c:out value='${formMessage}'/></textarea>
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-primary">Gửi Tin Nhắn</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <div class="col-lg-6" data-aos="fade-left" data-aos-delay="100">
                <div class="contact-map-wrapper">
                    <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3736.0772830022383!2d105.91511921488318!3d20.54787238627004!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135c7145c537b21%3A0xc9a7c11a428b8aa7!2zUGjDuiBMw70sIEjDoCBOYW0sIFZpZXRuYW0!5e0!3m2!1sen!2s!4v1672076940321!5m2!1sen!2s"
                            width="100%" height="500" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"
                            title="Bản đồ vị trí công ty"></iframe>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Business Hours Section (Example: Static, can be made dynamic if needed) -->
<section class="business-hours-section section-padding">
    <div class="container">
        <div class="section-title text-center" data-aos="fade-up">
            <h2>Giờ Làm Việc</h2>
            <p>Chúng tôi sẵn lòng đón tiếp quý khách tại văn phòng theo lịch làm việc dưới đây.</p>
        </div>

        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="business-hours-content" data-aos="fade-up" data-aos-delay="100">
                    <ul class="business-hours-list">
                        <li><span>Thứ Hai - Thứ Sáu:</span> <span>8:00 - 17:30</span></li>
                        <li><span>Thứ Bảy:</span> <span>8:00 - 12:00</span></li>
                        <li><span>Chủ Nhật:</span> <span class="text-danger">Đóng cửa</span></li>
                    </ul>
                    <p class="text-center mt-3 fst-italic">* Để được phục vụ tốt nhất, vui lòng đặt lịch hẹn trước khi đến.</p>
                </div>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/includes/footer.jsp" />