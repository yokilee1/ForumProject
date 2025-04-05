$(function(){
    // 初始化文件上传插件
    bsCustomFileInput.init();

    // 头像预览
    $('#head-image').change(function(){
        var file = this.files[0];
        if(file){
            var reader = new FileReader();
            reader.onload = function(e){
                $('.avatar-preview img').attr('src', e.target.result);
            }
            reader.readAsDataURL(file);
        }
    });

    // 密码强度检测
    $('#new-password').on('input', function(){
        var password = $(this).val();
        var strength = checkPasswordStrength(password);
        updatePasswordStrengthBar(strength);
    });

    // 确认密码验证
    $('#confirm-password').on('input', function(){
        var password = $('#new-password').val();
        var confirm = $(this).val();
        if(confirm && password !== confirm){
            $(this).addClass('is-invalid');
        } else {
            $(this).removeClass('is-invalid');
        }
    });

    // 密码强度检测函数
    function checkPasswordStrength(password) {
        if(!password) return 0;
        
        var strength = 0;
        // 长度检查
        if(password.length >= 8) strength++;
        // 包含数字
        if(/\d/.test(password)) strength++;
        // 包含小写字母
        if(/[a-z]/.test(password)) strength++;
        // 包含大写字母
        if(/[A-Z]/.test(password)) strength++;
        // 包含特殊字符
        if(/[^A-Za-z0-9]/.test(password)) strength++;
        
        return Math.min(strength, 3);
    }

    // 更新密码强度指示器
    function updatePasswordStrengthBar(strength) {
        var $bar = $('.password-strength-bar');
        $bar.removeClass('strength-weak strength-medium strength-strong');
        
        if(strength === 0) {
            $bar.css('width', '0');
        } else if(strength === 1) {
            $bar.addClass('strength-weak');
        } else if(strength === 2) {
            $bar.addClass('strength-medium');
        } else {
            $bar.addClass('strength-strong');
        }
    }

    // 表单提交前验证
    $('form').on('submit', function(e){
        var $form = $(this);
        var $inputs = $form.find('input[required]');
        var isValid = true;

        $inputs.each(function(){
            if(!$(this).val()){
                $(this).addClass('is-invalid');
                isValid = false;
            } else {
                $(this).removeClass('is-invalid');
            }
        });

        if(!isValid){
            e.preventDefault();
        }
    });
}); 