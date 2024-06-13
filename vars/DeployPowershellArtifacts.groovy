def call(label){
    def path_ps_user_prof = "D:\\system\\applications\\cmder\\config"
    def path_ps_modules = "${path_ps_user_prof}\\PowerShell_Modules"

    def file_prof = "user_profile.ps1"
    def zero_space_module = "ZeroSpace"
    def comp_vdi_module = "CompressVDI"
    def dep_cyg_module = "DeployCygwin"

    bat returnStatus: true, script: """robocopy . ${path_ps_user_prof} ${file_prof}"""

    if (label.contains('Dell') || label.contains('MSI')){
        bat returnStatus: true, script: """robocopy /E . ${path_ps_modules} /XF ${file_prof} /XD ${zero_space_module}"""
    }

    if (label.contains('VBox_VM')) {
        bat returnStatus: true, script: """robocopy /E . ${path_ps_modules} /XF ${file_prof} /XD ${comp_vdi_module} ${dep_cyg_module}"""
    }
}