package com.datn.backendHN.controller;

import com.datn.backendHN.dto.BenhVienDto;
import com.datn.backendHN.dto.taiKhoanDto;
import com.datn.backendHN.dto.CreateBenhVienAccountDto;
import com.datn.backendHN.entity.ResponseObject;
import com.datn.backendHN.service.BenhVienService;
import com.datn.backendHN.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Controller", description = "API quản lý bệnh viện và tài khoản bệnh viện")
public class AdminController {

    @Autowired
    private BenhVienService benhVienService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    // Quản lý bệnh viện
    @Operation(
        summary = "Lấy danh sách tất cả bệnh viện",
        description = "API này trả về danh sách tất cả các bệnh viện đã đăng ký trong hệ thống. " +
                     "Mỗi bệnh viện bao gồm thông tin: ID, tên bệnh viện, địa chỉ, số điện thoại và ID tài khoản."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BenhVienDto.class),
                examples = @ExampleObject(value = "[{\"id\": 1, \"tenBenhVien\": \"Bệnh viện Đa khoa Hưng Yên\", \"diaChi\": \"POINT(106.0515 20.6464)\", \"soDienThoai\": \"02213888888\", \"idTaiKhoan\": 1}]"))),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/benh-vien")
    public ResponseEntity<List<BenhVienDto>> getAllBenhVien() {
        return ResponseEntity.ok(benhVienService.getAllBenhVien());
    }

    @Operation(
        summary = "Lấy thông tin bệnh viện theo ID",
        description = "API này trả về thông tin chi tiết của một bệnh viện dựa trên ID được cung cấp."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BenhVienDto.class),
                examples = @ExampleObject(value = "{\"id\": 1, \"tenBenhVien\": \"Bệnh viện Đa khoa Hưng Yên\", \"diaChi\": \"POINT(106.0515 20.6464)\", \"soDienThoai\": \"02213888888\", \"idTaiKhoan\": 1}"))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy bệnh viện"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/benh-vien/{id}")
    public ResponseEntity<BenhVienDto> getBenhVienById(
            @Parameter(description = "ID của bệnh viện cần lấy thông tin", example = "1") @PathVariable Integer id) {
        return ResponseEntity.ok(benhVienService.getBenhVienById(id));
    }

    @Operation(
        summary = "Tạo bệnh viện mới",
        description = "API này cho phép tạo một bệnh viện mới trong hệ thống. " +
                     "Địa chỉ phải được cung cấp dưới dạng WKT (Well-Known Text) với định dạng POINT(kinh_do vĩ_do)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo bệnh viện thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BenhVienDto.class),
                examples = @ExampleObject(value = "{\"id\": 1, \"tenBenhVien\": \"Bệnh viện Đa khoa Hưng Yên\", \"diaChi\": \"POINT(106.0515 20.6464)\", \"soDienThoai\": \"02213888888\", \"idTaiKhoan\": 1}"))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/benh-vien")
    public ResponseEntity<BenhVienDto> createBenhVien(
            @Parameter(description = "Thông tin bệnh viện cần tạo",
                schema = @Schema(implementation = BenhVienDto.class),
                examples = @ExampleObject(value = "{\"tenBenhVien\": \"Bệnh viện Đa khoa Hưng Yên\", \"diaChi\": \"POINT(106.0515 20.6464)\", \"soDienThoai\": \"02213888888\", \"idTaiKhoan\": 1}"))
            @Valid @RequestBody BenhVienDto dto) {
        return ResponseEntity.ok(benhVienService.createBenhVien(dto));
    }

    @Operation(
        summary = "Cập nhật thông tin bệnh viện",
        description = "API này cho phép cập nhật thông tin của một bệnh viện đã tồn tại."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BenhVienDto.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy bệnh viện"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/benh-vien/{id}")
    public ResponseEntity<BenhVienDto> updateBenhVien(
            @Parameter(description = "ID của bệnh viện cần cập nhật", example = "1") @PathVariable Integer id,
            @Parameter(description = "Thông tin bệnh viện cần cập nhật",
                schema = @Schema(implementation = BenhVienDto.class))
            @Valid @RequestBody BenhVienDto dto) {
        return ResponseEntity.ok(benhVienService.updateBenhVien(id, dto));
    }

    @Operation(
        summary = "Xóa bệnh viện",
        description = "API này cho phép xóa một bệnh viện khỏi hệ thống dựa trên ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy bệnh viện"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/benh-vien/{id}")
    public ResponseEntity<Void> deleteBenhVien(
            @Parameter(description = "ID của bệnh viện cần xóa", example = "1") @PathVariable Integer id) {
        benhVienService.deleteBenhVien(id);
        return ResponseEntity.ok().build();
    }

    // Quản lý tài khoản bệnh viện
    @Operation(
        summary = "Tạo tài khoản bệnh viện",
        description = "API này cho phép tạo tài khoản mới cho bệnh viện. " +
                     "Mật khẩu phải có ít nhất 8 ký tự. " +
                     "Số điện thoại phải có đúng 10 chữ số."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo tài khoản thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseObject.class),
                examples = @ExampleObject(value = "{\"status\": \"OK\", \"message\": \"Success\", \"data\": {\"id\": 1, \"email\": \"benhvien@example.com\", \"soDienThoai\": \"0123456789\", \"ten\": \"Bệnh viện Đa khoa Hưng Yên\", \"vaiTro\": \"benh_vien\"}}"))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/taikhoan/create-benh-vien")
    public ResponseEntity<ResponseObject<taiKhoanDto>> createTaiKhoanBenhVien(
            @Parameter(description = "Thông tin tài khoản bệnh viện cần tạo",
                schema = @Schema(implementation = CreateBenhVienAccountDto.class),
                examples = @ExampleObject(value = "{\"email\": \"benhvien@example.com\", \"matKhau\": \"password123\", \"soDienThoai\": \"0123456789\", \"ten\": \"Bệnh viện Đa khoa Hưng Yên\"}"))
            @RequestBody CreateBenhVienAccountDto dto) {
        try {
            taiKhoanDto taiKhoanDto = new taiKhoanDto();
            taiKhoanDto.setEmail(dto.getEmail());
            taiKhoanDto.setMatKhau(dto.getMatKhau());
            taiKhoanDto.setSoDienThoai(dto.getSoDienThoai());
            taiKhoanDto.setTen(dto.getTen());
            taiKhoanDto.setVaiTro("benh_vien");

            taiKhoanDto result = taiKhoanService.register(taiKhoanDto);
            return ResponseEntity.ok(new ResponseObject<taiKhoanDto>(HttpStatus.OK, "Success", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject<taiKhoanDto>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @Operation(
        summary = "Lấy danh sách tài khoản bệnh viện",
        description = "API này trả về danh sách tất cả các tài khoản bệnh viện trong hệ thống."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = taiKhoanDto.class),
                examples = @ExampleObject(value = "[{\"id\": 1, \"email\": \"benhvien@example.com\", \"soDienThoai\": \"0123456789\", \"ten\": \"Bệnh viện Đa khoa Hưng Yên\", \"vaiTro\": \"benh_vien\"}]"))),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/taikhoan/benh-vien")
    public ResponseEntity<List<taiKhoanDto>> getAllTaiKhoanBenhVien() {
        return ResponseEntity.ok(taiKhoanService.getAllTaiKhoanByVaiTro("benh_vien"));
    }

    @Operation(
        summary = "Lấy thông tin tài khoản bệnh viện theo ID",
        description = "API này trả về thông tin chi tiết của một tài khoản bệnh viện dựa trên ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = taiKhoanDto.class),
                examples = @ExampleObject(value = "{\"id\": 1, \"email\": \"benhvien@example.com\", \"soDienThoai\": \"0123456789\", \"ten\": \"Bệnh viện Đa khoa Hưng Yên\", \"vaiTro\": \"benh_vien\"}"))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/taikhoan/benh-vien/{id}")
    public ResponseEntity<taiKhoanDto> getTaiKhoanBenhVienById(
            @Parameter(description = "ID của tài khoản cần lấy thông tin", example = "1") @PathVariable Integer id) {
        return ResponseEntity.ok(taiKhoanService.getTaiKhoanById(id));
    }

    @Operation(
        summary = "Cập nhật tài khoản bệnh viện",
        description = "API này cho phép cập nhật thông tin của một tài khoản bệnh viện."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = taiKhoanDto.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/taikhoan/benh-vien/{id}")
    public ResponseEntity<?> updateTaiKhoanBenhVien(
            @Parameter(description = "ID của tài khoản cần cập nhật", example = "1") @PathVariable Integer id,
            @Parameter(description = "Thông tin tài khoản cần cập nhật",
                schema = @Schema(implementation = taiKhoanDto.class))
            @Valid @RequestBody taiKhoanDto dto) {
        try {
            dto.setVaiTro("benh_vien");
            taiKhoanDto result = taiKhoanService.updateProfile(id, dto);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Xóa tài khoản bệnh viện",
        description = "API này cho phép xóa một tài khoản bệnh viện khỏi hệ thống."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/taikhoan/benh-vien/{id}")
    public ResponseEntity<Void> deleteTaiKhoanBenhVien(
            @Parameter(description = "ID của tài khoản cần xóa", example = "1") @PathVariable Integer id) {
        taiKhoanService.deleteTaiKhoan(id);
        return ResponseEntity.ok().build();
    }
}
