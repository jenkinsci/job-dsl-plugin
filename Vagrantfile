# -*- mode: ruby -*-
# vi: set ft=ruby :

$script = <<SCRIPT
wget --quiet --output-document=/etc/yum.repos.d/jenkins.repo http://pkg.jenkins-ci.org/redhat-stable/jenkins.repo
rpm --import http://pkg.jenkins-ci.org/redhat-stable/jenkins-ci.org.key
yum --assumeyes --quiet install java-1.7.0-openjdk jenkins-1.554.2-1.1
service jenkins start
SCRIPT

Vagrant.configure("2") do |config|
    config.vm.box = "chef/centos-6.5"
    config.vm.network "forwarded_port", guest: 8080, host: 8081
    config.vm.provision "shell", inline: $script
end
