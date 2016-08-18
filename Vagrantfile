# -*- mode: ruby -*-
# vi: set ft=ruby :

$base_script = <<SCRIPT
sudo apt-get update
sudo apt-get install -y openjdk-7-jdk git
SCRIPT

$script = <<SCRIPT
sudo apt-get install -y daemon
wget -N -P /var/cache/wget --progress=dot:giga http://pkg.jenkins-ci.org/debian/binary/jenkins_${VERSION}_all.deb
sudo dpkg -i /var/cache/wget/jenkins_${VERSION}_all.deb
SCRIPT

jenkins_version = IO.read("gradle.properties")[/jenkinsVersion=(.*)/,1]

Vagrant.configure(2) do |config|
    config.vm.box = "ubuntu/trusty64"

    config.vm.synced_folder ".", "/vagrant", disabled: true

    config.vm.define "master", primary: true do |master|
        master.vm.network "forwarded_port", guest: 8080, host: 8081
        master.vm.provision "shell", inline: $base_script
        master.vm.provision "shell", inline: $script, env: { VERSION: jenkins_version }
    end

    config.vm.define "node", autostart: false do |node|
        node.vm.provision "shell", inline: $base_script
    end

    if Vagrant.has_plugin?("vagrant-cachier")
        config.cache.scope = :box
        config.cache.enable :generic, {
            "wget" => { cache_dir: "/var/cache/wget" },
        }
    end
end
