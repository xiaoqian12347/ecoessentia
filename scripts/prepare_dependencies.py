"""Fetch the pinned ECO build dependency using only the Python standard library."""
import hashlib
from pathlib import Path
import urllib.request

DESTINATION = Path(__file__).resolve().parents[1] / 'libs/ecoaeextension-1.2.0.jar'
URL = ('https://github.com/sddsd2332/NovaEngineering-ECOAEExtension/releases/'
       'download/1.2.0/Nova-Engineering-ECO-AE-Extension-1.2.0.jar')
SHA256 = 'c6c0bf0a3869c8f780296589aa5010d07ced5fa9524e1f584028a3e82bb03e2e'


def matches(path):
    if not path.is_file():
        return False
    digest = hashlib.sha256()
    with path.open('rb') as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b''):
            digest.update(chunk)
    return digest.hexdigest() == SHA256


def main():
    if matches(DESTINATION):
        print('ECO 1.2.0 dependency verified.')
        return
    DESTINATION.parent.mkdir(parents=True, exist_ok=True)
    temporary = DESTINATION.with_suffix('.jar.part')
    try:
        request = urllib.request.Request(URL, headers={'User-Agent': 'ECOEssentiaBuild/0.2.0'})
        with urllib.request.urlopen(request, timeout=60) as response, temporary.open('wb') as out:
            for chunk in iter(lambda: response.read(1024 * 1024), b''):
                out.write(chunk)
        if not matches(temporary):
            raise RuntimeError('ECO dependency SHA-256 mismatch; incomplete download was not installed.')
        temporary.replace(DESTINATION)
        print('ECO 1.2.0 dependency downloaded and verified.')
    finally:
        temporary.unlink(missing_ok=True)


if __name__ == '__main__':
    main()
