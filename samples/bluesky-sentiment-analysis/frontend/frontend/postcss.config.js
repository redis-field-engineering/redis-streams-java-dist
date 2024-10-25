module.exports = {
    plugins: [
      require("postcss-import")({
        addModulesDirectories: ["assets/css"]
      }),
      require("tailwindcss"),
      require("autoprefixer")
    ],
  };
  