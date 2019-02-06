#!/bin/bash

usage(){
	echo "Usage: $0 -v <desired version> -g <gpg password>"
	echo "Or if you want to be asked fro GPG password without printing to console: $0 -v <desired version>"
	exit 1
}

while getopts ":hv:g:" opt
do
   case ${opt} in
       v)
           VERSION=${OPTARG}
           ;;
       g)
           GPG_PASS=${OPTARG}
           ;;
       h)
           usage
           exit 0
           ;;

       \?)
           echo -e "Invalid option: -${OPTARG}"
           exit 1
           ;;

       :)
           echo -e "Option -${OPTARG} require argument."
           exit 2
           ;;
   esac
done

shift $(( ${OPTIND} - 1 ))

if [ $# -ne 0 ]
then
   echo "There are unexpected parameters!" 1>&2
   usage
   exit 1
fi

if [ -z "${VERSION}" ]
then
   echo "No version given!"
   usage
   exit 1
fi

if [ -z "${GPG_PASS}" ]
then
   echo "Enter gpg password: "
   read -s GPG_PASS
   if [ -z "${GPG_PASS}" ]
   then
      echo "No gpg password given!"
      usage
      exit 1
   fi
fi

API_VERSION=$(cat src/main/resources/GoodDataApiVersion)
FULL_VERSION=$VERSION+api$API_VERSION
DEV_VERSION=$(echo $VERSION | awk -F. '{$NF = $NF + 1;} 1' | sed 's/ /./g')-SNAPSHOT

sed -i.bak "s/\(<version>\)[0-9]*\.[0-9]*\.[0-9]*[^<]*\(<\/version>\)/\1${FULL_VERSION}\2/" README.md && rm -f README.md.bak
git commit -a -m "bump version"

mvn --batch-mode release:prepare release:perform -DreleaseVersion=${FULL_VERSION} -DdevelopmentVersion=${DEV_VERSION} -Darguments="-Dgpg.passphrase=${GPG_PASS}"
MVN_RET_VAL=$?
if [ $MVN_RET_VAL -ne 0 ]; then
    echo "Mvn build failed - please fix it and try to release again"
    echo "Removing last commit '$(git log -1 --pretty="%h %s")'"
    git reset --hard HEAD~1
    exit $MVN_RET_VAL
fi

git push origin --tags HEAD
