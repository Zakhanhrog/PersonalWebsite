<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<footer class="footer-area">
    <div class="container">
        <div class="row">
            <div class="col-lg-4 col-md-6">
                <div class="footer-widget">
                    <div class="footer-logo">
                        <h2><c:out value="${profile.name ne null ? profile.name : 'Ngô Thanh Bạch'}"/></h2>
                    </div>
                    <div class="footer-text">
                        <p>Với nhiều năm kinh nghiệm trong lĩnh vực kinh doanh và quản lý, tôi luôn nỗ lực để mang đến những giá trị tốt nhất cho khách hàng và đối tác.</p>
                    </div>
                    <div class="footer-social-icon">
                        <a href="#" target="_blank" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a>
                        <a href="#" target="_blank" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
                        <a href="#" target="_blank" aria-label="LinkedIn"><i class="fab fa-linkedin-in"></i></a>
                        <a href="#" target="_blank" aria-label="Instagram"><i class="fab fa-instagram"></i></a>
                    </div>
                </div>
            </div>
            <div class="col-lg-2 col-md-6">
                <div class="footer-widget">
                    <div class="footer-widget-heading">
                        <h3>Liên Kết</h3>
                    </div>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/">Trang Chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/about">Giới Thiệu</a></li>
                        <li><a href="${pageContext.request.contextPath}/projects">Dự Án</a></li>
                        <li><a href="${pageContext.request.contextPath}/blog">Blog</a></li>
                        <li><a href="${pageContext.request.contextPath}/contact">Liên Hệ</a></li>
                    </ul>
                </div>
            </div>
            <div class="col-lg-3 col-md-6">
                <div class="footer-widget">
                    <div class="footer-widget-heading">
                        <h3>Dịch Vụ Chính</h3>
                    </div>
                    <ul>
                        <li><a href="#">Phát triển Bất động sản</a></li>
                        <li><a href="#">Đầu tư Khu công nghiệp</a></li>
                        <li><a href="#">Tư vấn Đầu tư</a></li>
                        <li><a href="#">Quản lý Dự án</a></li>
                        <li><a href="#">Thương mại Quốc tế</a></li>
                    </ul>
                </div>
            </div>
            <div class="col-lg-3 col-md-6">
                <div class="footer-widget">
                    <div class="footer-widget-heading">
                        <h3>Thông Tin Liên Hệ</h3>
                    </div>
                    <div class="footer-text contact-info">
                        <p><i class="fas fa-map-marker-alt"></i> <c:out value="${profile.companyAddress ne null ? profile.companyAddress : 'Địa chỉ chưa cập nhật'}"/></p>
                        <p><i class="fas fa-phone"></i> <a href="tel:${profile.phoneNumber}"><c:out value="${profile.phoneNumber ne null ? profile.phoneNumber : 'Chưa cập nhật'}"/></a></p>
                        <p><i class="fas fa-envelope"></i> <a href="mailto:${profile.email}"><c:out value="${profile.email ne null ? profile.email : 'Chưa cập nhật'}"/></a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="copyright-area">
        <div class="container">
            <div class="row">
                <div class="col-12 text-center">
                    <div class="copyright-text">
                        <p>© ${java.time.Year.now().getValue()} | Bản quyền thuộc về <c:out value="${profile.name ne null ? profile.name : 'Website Owner'}" />
                            <c:if test="${profile.companyName ne null && not empty profile.companyName}">
                                | <c:out value="${profile.companyName}"/>
                            </c:if>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</footer>

<script src="https://code.jquery.com/jquery-3.6.3.min.js" integrity="sha256-pvPw+upLPUjgMXY0G+8O0xUf+/Im1MZjXxxgOcBQBXU=" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
<script src="https://unpkg.com/aos@next/dist/aos.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.11.4/gsap.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.11.4/ScrollTrigger.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/main.js"></script>

</body>
</html>