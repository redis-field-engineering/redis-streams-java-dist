provider "aws" {
  region = "${var.aws_region}"
}

resource "aws_key_pair" "sshKey" {
    key_name = "sessions-test-ssh-key"
    public_key = file("${var.ssh_key_file}")  
}

resource "aws_instance" "fe-streams-test-vm" {
  ami           = "ami-02b71e4c18caedcb5"
  instance_type = "t2.2xlarge"
  key_name = aws_key_pair.sshKey.key_name  

  tags = {
    name = "fe-streams-test-vm"
  }

  root_block_device {
    volume_size = 60
    volume_type = "gp3"
  }

  user_data = <<-EOF
#!/bin/bash
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk git docker.io docker-compose

sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/download/v2.22.0/docker-compose-linux-x86_64 -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

#start Docker service

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Git repository clone with error handling
git clone https://github.com/redis-field-engineering/redis-streams-java-dist.git || { echo "Git clone failed"; exit 1; }

cd redis-streams-java-dist/samples/bluesky-sentiment-analysis

# Build the project using OpenJDK with error handling
./gradlew bootjar || { echo "Gradle build failed"; exit 1; }

# Spin up Docker containers with error handling
docker compose up --scale consumer=3 -d || { echo "Docker containers failed to start"; exit 1; }
  EOF
}

data "aws_route53_zone" "redisdemo_zone"{
    name = "${var.aws_dns_zone}"
}

resource "aws_route53_record" "fe-streams-test-vm-a-record" {
  zone_id = data.aws_route53_zone.redisdemo_zone.zone_id
  name = "bsky.streams.demo"
  type = "A"
  ttl = "300"
  records = [aws_instance.fe-streams-test-vm.public_ip]
  depends_on = [aws_instance.fe-streams-test-vm]
}