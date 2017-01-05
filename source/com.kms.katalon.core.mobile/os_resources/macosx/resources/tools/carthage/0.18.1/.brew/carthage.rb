class Carthage < Formula
  desc "Decentralized dependency manager for Cocoa"
  homepage "https://github.com/Carthage/Carthage"
  url "https://github.com/Carthage/Carthage.git",
      :tag => "0.18.1",
      :revision => "84747d78c04e42e175059d5fa35468e50663c503",
      :shallow => false
  head "https://github.com/Carthage/Carthage.git", :shallow => false

  depends_on :xcode => ["7.3", :build]

  def install
    system "make", "prefix_install", "PREFIX=#{prefix}"
    bash_completion.install "Source/Scripts/carthage-bash-completion" => "carthage"
    zsh_completion.install "Source/Scripts/carthage-zsh-completion" => "_carthage"
    fish_completion.install "Source/Scripts/carthage-fish-completion" => "carthage.fish"
  end

  test do
    (testpath/"Cartfile").write 'github "jspahrsummers/xcconfigs"'
    system bin/"carthage", "update"
  end
end
