# -*- mode: ruby -*-
# vi: set ft=ruby :

$script = <<SCRIPT
wget --quiet --output-document=/etc/yum.repos.d/jenkins.repo http://pkg.jenkins-ci.org/redhat-stable/jenkins.repo
rpm --import http://pkg.jenkins-ci.org/redhat-stable/jenkins-ci.org.key
yum --assumeyes --quiet install java-1.7.0-openjdk jenkins-1.565.1-1.1 git
service jenkins start
SCRIPT

$node_script = <<SCRIPT
yum --assumeyes --quiet install java-1.7.0-openjdk git
SCRIPT

Vagrant.configure("2") do |config|
    config.vm.box = "chef/centos-7.0"

    config.vm.synced_folder ".", "/vagrant", disabled: true

    config.vm.define "master", primary: true do |master|
      master.vm.network "forwarded_port", guest: 8080, host: 8081
      master.vm.provision "shell", inline: $script
    end

    config.vm.define "node", autostart: false do |node|
      node.vm.provision "shell", inline: $node_script
    end
end
