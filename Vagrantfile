# -*- mode: ruby -*-
# vi: set ft=ruby :

$script = <<SCRIPT
wget -q -O - http://pkg.jenkins-ci.org/debian-stable/jenkins-ci.org.key | apt-key add -
echo "deb http://pkg.jenkins-ci.org/debian-stable binary/" >> /etc/apt/sources.list
apt-get update
apt-get -y install jenkins=1.554.1
service jenkins start
SCRIPT

Vagrant.configure("2") do |config|
    config.vm.box = "ubuntu/trusty64"
    config.vm.network "forwarded_port", guest: 8080, host: 8081
    config.vm.provision "shell", inline: $script
end
