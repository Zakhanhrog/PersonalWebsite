<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Quản lý Hồ Sơ Admin</title>
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
  <style>
    body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji"; font-size: 0.9rem; background-color: #f4f6f9;}
    .admin-header { padding: 1rem 1.5rem; background-color: #007bff; color: #fff; margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: center; }
    .admin-header h1 { font-size: 1.75rem; margin-bottom: 0; }
    .admin-header .btn { font-size: 0.875rem; }
    .card { border: none; box-shadow: 0 0 1px rgba(0,0,0,.125),0 1px 3px rgba(0,0,0,.2); margin-bottom: 1.5rem; }
    .card-title-custom { font-size: 1.1rem; font-weight: 600; color: #007bff; padding-bottom: 0.75rem; border-bottom: 1px solid #eee; margin-bottom: 1.25rem; }
    .form-section { margin-bottom: 2.5rem; }
    .table th { font-weight: 600; }
    .table td .btn { padding: .25rem .5rem; font-size: .8rem; }
    .btn-block-custom { display: block; width: 100%; }
    label { font-weight: 500; margin-bottom: .3rem; }
    .current-photo-preview { max-width: 100px; max-height: 100px; margin-bottom: 10px; border:1px solid #ddd; padding: 2px; background-color: #fff;}
  </style>
</head>
<body>
<jsp:include page="/admin/includes/admin-header.jsp" />

<div class="container-fluid px-3">
  <c:if test="${not empty message}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <c:out value="${message}"/>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
    </div>
  </c:if>
  <c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:out value="${error}"/>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
    </div>
  </c:if>

  <div class="card form-section">
    <div class="card-body">
      <h4 class="card-title-custom">Thông Tin Cá Nhân & Công Ty</h4>
      <form action="${pageContext.request.contextPath}/admin/profile" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="updateProfile">
        <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="name">Họ tên *</label>
            <input type="text" class="form-control form-control-sm" id="name" name="name" value="<c:out value='${profile.name}'/>" required>
          </div>
          <div class="form-group col-md-6">
            <label for="position">Chức vụ</label>
            <input type="text" class="form-control form-control-sm" id="position" name="position" value="<c:out value='${profile.position}'/>">
          </div>
        </div>
        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="companyName">Tên công ty</label>
            <input type="text" class="form-control form-control-sm" id="companyName" name="companyName" value="<c:out value='${profile.companyName}'/>">
          </div>
          <div class="form-group col-md-6">
            <label for="companyTaxId">Mã số thuế</label>
            <input type="text" class="form-control form-control-sm" id="companyTaxId" name="companyTaxId" value="<c:out value='${profile.companyTaxId}'/>">
          </div>
        </div>
        <div class="form-group">
          <label for="companyAddress">Địa chỉ công ty</label>
          <input type="text" class="form-control form-control-sm" id="companyAddress" name="companyAddress" value="<c:out value='${profile.companyAddress}'/>">
        </div>
        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="phoneNumber">Số điện thoại</label>
            <input type="text" class="form-control form-control-sm" id="phoneNumber" name="phoneNumber" value="<c:out value='${profile.phoneNumber}'/>">
          </div>
          <div class="form-group col-md-6">
            <label for="email">Email *</label>
            <input type="email" class="form-control form-control-sm" id="email" name="email" value="<c:out value='${profile.email}'/>" required>
          </div>
        </div>
        <div class="form-group">
          <label for="bio">Giới thiệu bản thân (Bio)</label>
          <textarea class="form-control form-control-sm" id="bio" name="bio" rows="5"><c:out value='${profile.bio}'/></textarea>
        </div>

        <div class="form-group">
          <label>Ảnh đại diện hiện tại:</label><br>
          <c:if test="${not empty profile.photoUrl}">
            <c:set var="currentProfilePhotoSource">
              <c:choose>
                <c:when test="${fn:startsWith(profile.photoUrl, 'http')}">
                  ${profile.photoUrl}
                </c:when>
                <c:otherwise>
                  ${pageContext.request.contextPath}/uploads/${profile.photoUrl}
                </c:otherwise>
              </c:choose>
            </c:set>
            <img src="${currentProfilePhotoSource}"
                 alt="Ảnh của ${profile.name}"
                 class="current-photo-preview"
                 onerror="this.src='${pageContext.request.contextPath}/resources/images/default-profile-full-placeholder.jpg'; this.onerror=null;">
            <div class="form-check mb-2">
              <input class="form-check-input" type="checkbox" name="deletePhoto" value="true" id="deletePhotoCheck">
              <label class="form-check-label" for="deletePhotoCheck">
                Xóa ảnh hiện tại (sẽ không có ảnh nếu không chọn ảnh mới)
              </label>
            </div>
          </c:if>
          <c:if test="${empty profile.photoUrl}">
            <p class="text-muted small">Chưa có ảnh đại diện.</p>
          </c:if>
        </div>
        <div class="form-group">
          <label for="photoFile">Chọn ảnh đại diện mới (nếu muốn thay đổi/thêm):</label>
          <input type="file" class="form-control-file" id="photoFile" name="photoFile" accept="image/png, image/jpeg, image/gif">
          <small class="form-text text-muted">Để trống nếu không muốn thay đổi ảnh. Ảnh tối đa 10MB.</small>
        </div>

        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Cập nhật Hồ Sơ</button>
      </form>
    </div>
  </div>

  <div class="card form-section">
    <div class="card-body">
      <h4 class="card-title-custom">Quản lý Kỹ Năng</h4>
      <h5>Thêm Kỹ Năng Mới</h5>
      <form action="${pageContext.request.contextPath}/admin/profile" method="post" class="mb-4">
        <input type="hidden" name="action" value="addSkill">
        <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
        <div class="form-row align-items-end">
          <div class="form-group col-md-4">
            <label for="skillName">Tên kỹ năng *</label>
            <input type="text" class="form-control form-control-sm" id="skillName" name="skillName" required>
          </div>
          <div class="form-group col-md-3">
            <label for="skillLevel">Mức độ (%) *</label>
            <input type="number" class="form-control form-control-sm" id="skillLevel" name="skillLevel" min="0" max="100" required>
          </div>
          <div class="form-group col-md-3">
            <label for="skillCategory">Phân loại</label>
            <input type="text" class="form-control form-control-sm" id="skillCategory" name="skillCategory" placeholder="Chuyên môn, Kỹ năng mềm,...">
          </div>
          <div class="form-group col-md-2">
            <button type="submit" class="btn btn-success btn-block-custom"><i class="fas fa-plus"></i> Thêm</button>
          </div>
        </div>
      </form>

      <h5>Danh Sách Kỹ Năng Hiện Tại</h5>
      <c:choose>
        <c:when test="${not empty skillsList}">
          <div class="table-responsive">
            <table class="table table-striped table-sm">
              <thead><tr><th>Tên Kỹ Năng</th><th>Mức Độ</th><th>Phân Loại</th><th>Thao Tác</th></tr></thead>
              <tbody>
              <c:forEach items="${skillsList}" var="skill">
                <tr>
                  <td><c:out value="${skill.name}"/></td>
                  <td>${skill.level}%</td>
                  <td><c:out value="${skill.category}"/></td>
                  <td>
                    <form action="${pageContext.request.contextPath}/admin/profile" method="post" style="display: inline;">
                      <input type="hidden" name="action" value="deleteSkill">
                      <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                      <input type="hidden" name="skillId" value="${skill.id}">
                      <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Bạn có chắc muốn xóa kỹ năng này?');"><i class="fas fa-trash"></i></button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:when>
        <c:otherwise><p class="text-muted">Chưa có kỹ năng nào được thêm.</p></c:otherwise>
      </c:choose>
    </div>
  </div>

  <div class="card form-section">
    <div class="card-body">
      <h4 class="card-title-custom">Quản lý Học Vấn</h4>
      <h5>Thêm Mục Học Vấn Mới</h5>
      <form action="${pageContext.request.contextPath}/admin/profile" method="post" class="mb-4">
        <input type="hidden" name="action" value="addEducation">
        <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
        <div class="form-row">
          <div class="form-group col-md-6"><label for="eduSchoolName">Tên trường/tổ chức *</label><input type="text" class="form-control form-control-sm" id="eduSchoolName" name="eduSchoolName" required></div>
          <div class="form-group col-md-6"><label for="eduDegree">Bằng cấp/Chứng chỉ</label><input type="text" class="form-control form-control-sm" id="eduDegree" name="eduDegree"></div>
        </div>
        <div class="form-row">
          <div class="form-group col-md-5"><label for="eduFieldOfStudy">Chuyên ngành</label><input type="text" class="form-control form-control-sm" id="eduFieldOfStudy" name="eduFieldOfStudy"></div>
          <div class="form-group col-md-3"><label for="eduStartYear">Năm bắt đầu</label><input type="text" class="form-control form-control-sm" id="eduStartYear" name="eduStartYear" placeholder="YYYY"></div>
          <div class="form-group col-md-3"><label for="eduEndYear">Năm kết thúc</label><input type="text" class="form-control form-control-sm" id="eduEndYear" name="eduEndYear" placeholder="YYYY hoặc Hiện tại"></div>
        </div>
        <div class="form-group"><label for="eduDescription">Mô tả thêm</label><textarea class="form-control form-control-sm" id="eduDescription" name="eduDescription" rows="2"></textarea></div>
        <button type="submit" class="btn btn-success"><i class="fas fa-plus"></i> Thêm Học Vấn</button>
      </form>

      <h5>Danh Sách Học Vấn Hiện Tại</h5>
      <c:choose>
        <c:when test="${not empty educationsList}">
          <div class="table-responsive">
            <table class="table table-striped table-sm">
              <thead><tr><th>Trường/Tổ chức</th><th>Bằng cấp</th><th>Chuyên ngành</th><th>Thời gian</th><th>Mô tả</th><th>Thao Tác</th></tr></thead>
              <tbody>
              <c:forEach items="${educationsList}" var="edu">
                <tr>
                  <td><c:out value="${edu.schoolName}"/></td>
                  <td><c:out value="${edu.degree}"/></td>
                  <td><c:out value="${edu.fieldOfStudy}"/></td>
                  <td><c:out value="${edu.startYear}"/> - <c:out value="${edu.endYear}"/></td>
                  <td><c:out value="${edu.description}"/></td>
                  <td>
                    <form action="${pageContext.request.contextPath}/admin/profile" method="post" style="display: inline;">
                      <input type="hidden" name="action" value="deleteEducation">
                      <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                      <input type="hidden" name="eduId" value="${edu.id}">
                      <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Xóa mục học vấn này?');"><i class="fas fa-trash"></i></button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:when>
        <c:otherwise><p class="text-muted">Chưa có thông tin học vấn.</p></c:otherwise>
      </c:choose>
    </div>
  </div>

  <div class="card form-section">
    <div class="card-body">
      <h4 class="card-title-custom">Quản lý Kinh Nghiệm Làm Việc</h4>
      <h5>Thêm Mục Kinh Nghiệm Mới</h5>
      <form action="${pageContext.request.contextPath}/admin/profile" method="post" class="mb-4">
        <input type="hidden" name="action" value="addExperience">
        <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
        <div class="form-row">
          <div class="form-group col-md-6"><label for="expCompanyName">Tên công ty *</label><input type="text" class="form-control form-control-sm" id="expCompanyName" name="expCompanyName" required></div>
          <div class="form-group col-md-6"><label for="expPosition">Vị trí *</label><input type="text" class="form-control form-control-sm" id="expPosition" name="expPosition" required></div>
        </div>
        <div class="form-row">
          <div class="form-group col-md-6"><label for="expStartDate">Ngày bắt đầu (YYYY-MM-DD)</label><input type="date" class="form-control form-control-sm" id="expStartDate" name="expStartDate"></div>
          <div class="form-group col-md-6"><label for="expEndDate">Ngày kết thúc (YYYY-MM-DD)</label><input type="date" class="form-control form-control-sm" id="expEndDate" name="expEndDate"><small class="form-text text-muted">Để trống nếu là công việc hiện tại.</small></div>
        </div>
        <div class="form-group"><label for="expDescription">Mô tả công việc/Trách nhiệm</label><textarea class="form-control form-control-sm" id="expDescription" name="expDescription" rows="3"></textarea></div>
        <button type="submit" class="btn btn-success"><i class="fas fa-plus"></i> Thêm Kinh Nghiệm</button>
      </form>

      <h5>Danh Sách Kinh Nghiệm Hiện Tại</h5>
      <c:choose>
        <c:when test="${not empty experiencesList}">
          <div class="table-responsive">
            <table class="table table-striped table-sm">
              <thead><tr><th>Công ty</th><th>Vị trí</th><th>Thời gian</th><th>Mô tả</th><th>Thao Tác</th></tr></thead>
              <tbody>
              <c:forEach items="${experiencesList}" var="exp">
                <tr>
                  <td><c:out value="${exp.companyName}"/></td>
                  <td><c:out value="${exp.position}"/></td>
                  <td>
                    <fmt:formatDate value="${exp.startDate}" pattern="MM/yyyy"/> -
                    <c:choose>
                      <c:when test="${not empty exp.endDate}"><fmt:formatDate value="${exp.endDate}" pattern="MM/yyyy"/></c:when>
                      <c:otherwise>Hiện tại</c:otherwise>
                    </c:choose>
                  </td>
                  <td style="white-space: pre-line; max-width: 300px; overflow-y: auto; max-height: 100px;"><small><c:out value="${exp.descriptionResponsibilities}"/></small></td>
                  <td>
                    <form action="${pageContext.request.contextPath}/admin/profile" method="post" style="display: inline;">
                      <input type="hidden" name="action" value="deleteExperience">
                      <input type="hidden" name="org.apache.catalina.filters.CSRF_NONCE" value="${sessionScope['org.apache.catalina.filters.CSRF_NONCE']}">
                      <input type="hidden" name="expId" value="${exp.id}">
                      <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Xóa mục kinh nghiệm này?');"><i class="fas fa-trash"></i></button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:when>
        <c:otherwise><p class="text-muted">Chưa có thông tin kinh nghiệm làm việc.</p></c:otherwise>
      </c:choose>
    </div>
  </div>

</div>

<footer class="text-center py-4 mt-4 bg-light">
  <p class="mb-0">© ${java.time.Year.now().getValue()} Admin Panel - PersonalWebsite</p>
</footer>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
  $(document).ready(function() {
    window.setTimeout(function() {
      $(".alert-success, .alert-danger").fadeTo(500, 0).slideUp(500, function(){
        $(this).remove();
      });
    }, 7000);

    // UX improvements for photo upload
    $('#photoFile').on('change', function() {
      if ($(this).val()) {
        $('#deletePhotoCheck').prop('checked', false);
      }
    });
    $('#deletePhotoCheck').on('change', function() {
      if ($(this).is(':checked')) {
        $('#photoFile').val('');
      }
    });
  });
</script>
</body>
</html>