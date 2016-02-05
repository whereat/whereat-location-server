# Contribution Guidelines

Thanks for contributing to where@!  To get started:

1. Sign the [Contributor Agreement](https://www.clahub.com/agreements/whereat/whereat-location-server).
2. Fork the repo and do work in a feature branch.
3. Write tests for all new code.
4. Make sure all new and pre-existing tests pass.
5. Issue a pull request.

If you have any questions, open an issue or join the [contributor mailing list](https://lists.riseup.net/www/info/whereat-contrib).

# Being Excellent to One Another

We want contributing to Where@ to be a fun and empowering experience for everyone involved. Toward that end, we ask all contributors to adhere to the [Geek Feminism Code of Conduct](http://geekfeminism.org/about/code-of-conduct/) and encourage them to incorporate Recurse Center's [Five Social Rules](https://www.recurse.com/manual#sub-sec-social-rules) in their interactions.

# Tooling Setup Guide
* Set up [whereat/whereat-bash](https://github.com/whereat/whereat-bash)
* `$WHAT_ROOT`, `$WHAT_KEY` and `$WHAT_SCRIPTS` must be defined in your environment
* Run `whereat-bash/src/build/build-location-server.sh`
    1. This will move into your `$WHAT_ROOT` and checkout `whereat/whereat-location-server` source code
    2. This will then checkout the `whereat/whereat-location-server` docker image and run it, mounting the checked out source code
    3. Code modified locally will now be reflected in the docker image
    

# Building
This will build a docker container and load the image into your local Docker instance.
First, create a temporary directory for packer to use:
```
$ mkdir ~/tmp
```
Next, run packer, passing in the temporary directory to use:
```
$ TMPDIR=~/tmp packer build packer.json
```
To run the generated image:
```
docker run -it whereat/whereat-location-server bash
```