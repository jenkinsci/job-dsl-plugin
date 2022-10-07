// use the stock icon
folder('stock')

// use https://github.com/jenkinsci/custom-folder-icon-plugin for custom icons
userContent('customFolderIcons/custom.png', streamFileFromWorkspace('custom.png'))
folder('custom') {
    icon {
        customFolderIcon {
            foldericon('custom.png')
        }
    }
}

// use https://github.com/jenkinsci/custom-folder-icon-plugin for ionicons
folder('ionicon') {
    icon {
        ioniconFolderIcon {
            ionicon('jenkins')
        }
    }
}

// use https://github.com/jenkinsci/custom-folder-icon-plugin for build status icon
folder('build-status') {
    icon {
        buildStatusFolderIcon()
    }
}
