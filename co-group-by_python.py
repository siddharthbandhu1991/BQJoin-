import apache_beam as beam
import pandas as pd

class LoadExcelData(beam.DoFn):
    def process(self, element):
        # print(element.values())
        df = pd.read_excel(element['file_path'], sheet_name=element['sheet_name'])
        df['key'] = df['subject_id'].astype(str)+df['hadm_id'].astype(str)
        # print(df.to_dict())
        df_dict = df.to_dict(orient='records')
        # print(df_dict)
        yield df_dict


class ConvertKVPair(beam.DoFn):
    def process(self, element):
        # print(type(element))
        # print(element)
        for item in element:
            yield (item['key'], item)


class JoinTabs(beam.DoFn):
    def process(self, element, other_tab_data):
        other_tab_data_dict = {row['subject_id', 'hadm_id']: row for row in other_tab_data}
        for row in element:
            key = row['common_key_column']
            if key in other_tab_data_dict:
                yield {**row, **other_tab_data_dict[key]}

def run():
    with beam.Pipeline() as pipeline:
        # Load data from first tab
        data_from_tab1 = (pipeline
                          | 'Read Tab 1 Data' >> beam.Create([{'file_path': 'Sample_data.xlsx', 'sheet_name': 'Clinical Notes'}])
                          | 'Load Tab 1 Data' >> beam.ParDo(LoadExcelData())
                          | 'Convert to key,value pair tab 1' >> beam.ParDo(ConvertKVPair()))

        # Load data from second tab
        data_from_tab2 = (pipeline
                          | 'Read Tab 2 Data' >> beam.Create([{'file_path': 'Sample_data.xlsx', 'sheet_name': 'ICD Diagnosis'}])
                          | 'Load Tab 2 Data' >> beam.ParDo(LoadExcelData())
                          | 'Convert to key,value pair tab 2' >> beam.ParDo(ConvertKVPair()))

        # Join two tabs using a common key column
        joined_data = (
            {'data_from_tab1': data_from_tab1, 'data_from_tab2': data_from_tab2}
            | 'Join Tabs' >> beam.CoGroupByKey()
        )

        # Write the joined data to a new Excel file
        # joined_data | 'Write Joined Data to Excel' >> beam.io.WriteToText('joined_data.txt', file_name_suffix='.csv')
        joined_data | 'Write' >> beam.io.WriteToText('Sample_data.csv')

if __name__ == '__main__':
    run()
