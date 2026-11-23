"""Check a built release's namespace, resource graph, metadata and bytecode."""
import argparse
import json
from pathlib import Path
from zipfile import ZipFile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('jar', type=Path)
    args = parser.parse_args()
    with ZipFile(args.jar) as release:
        names = set(release.namelist())
        metadata = json.loads(release.read('mcmod.info'))[0]
        assert metadata['modid'] == 'ecoessentia'
        assert metadata['authorList'] == ['xiaoqian']
        assert not any('xiaoqian_ecoessentia' in name for name in names)
        assert 'META-INF/LICENSE' in names and 'META-INF/NOTICE.md' in names
        assert 'mixins.ecoessentia.refmap.json' in names

        def reference(ref, model=False):
            if not ref.startswith('ecoessentia:'):
                return
            path = ref.split(':', 1)[1]
            choices = ([f'assets/ecoessentia/models/{path}.json',
                        f'assets/ecoessentia/models/block/{path}.json'] if model
                       else [f'assets/ecoessentia/textures/{path}.png'])
            assert any(choice in names for choice in choices), ref

        def walk(data):
            if isinstance(data, dict):
                for key, value in data.items():
                    if key in ('parent', 'model') and isinstance(value, str):
                        reference(value, model=True)
                    if key == 'textures':
                        for ref in value.values():
                            reference(ref)
                    walk(value)
            elif isinstance(data, list):
                for value in data:
                    walk(value)

        for name in names:
            if name.endswith('.class'):
                assert name.startswith('com/xiaoqian/ecoessentia/'), name
                assert not name.endswith('Test.class'), name
                assert int.from_bytes(release.read(name)[6:8], 'big') == 52, name
            if name.endswith('.json'):
                walk(json.loads(release.read(name)))
        for size, level in [('16m', 'LE4'), ('64m', 'LE6'), ('256m', 'LE9')]:
            assert f'assets/ecoessentia/models/item/estorage_cell_essentia_{size}.json' in names
            language = release.read('assets/ecoessentia/lang/zh_cn.lang').decode('utf-8')
            assert f'item.ecoessentia.estorage_cell_essentia_{size}.name=' in language
            assert level in language and '源质存储矩阵' in language
        print(f"PASS: {args.jar.name}; metadata, namespace, resource graph, license and Java 8 bytecode.")


if __name__ == '__main__':
    main()
