return [
    git_repo_url: 'gdrive_backup_docs_repo:Serhii5465/gdrive_backup_docs.git',
    git_branch: 'main'
    stash_includes: '*.py',
    stash_excludes: '',
    command: 'robocopy /copyall . D:\\system\\applications\\cygwin64\\home\\raisnet\\scripts\\gdrive_backup_docs'
]