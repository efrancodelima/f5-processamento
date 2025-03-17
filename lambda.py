import json
import boto3
import os
import zipfile
from io import BytesIO

def lambda_handler(event, context):
    # Pega os detalhes do evento
    # job_id = event['detail']['jobId']
    job_id = '1742169170751-d3owk6'
    bucket_name = "main-357"
    job_output_directory = "2/1/output/"
    
    # Lista os arquivos do diretório de output do job
    s3_client = boto3.client('s3')
    response = s3_client.list_objects_v2(Bucket=bucket_name, Prefix=job_output_directory)

    # Armazena os endereços dos arquivos que serão compactados em um array
    files_to_compress = []
    if 'Contents' in response:
        for obj in response['Contents']:
            file_key = obj['Key']
            if not file_key.endswith('.mp4'): # Ignora arquivos mp4
                files_to_compress.append(file_key)
    
    # Se não houver arquivos no array, encerra
    if not files_to_compress:
        print("Nenhum arquivo a ser compactado.")
        return {
            'statusCode': 200,
            'body': json.dumps("Nenhum arquivo foi compactado.")
        }
    
    # Cria o arquivo ZIP em memória
    zip_buffer = BytesIO()
    with zipfile.ZipFile(zip_buffer, 'w') as zip_file:
        for file_key in files_to_compress:
            # Faz o download do arquivo para memória
            file_obj = s3_client.get_object(Bucket=bucket_name, Key=file_key)
            file_data = file_obj['Body'].read()
            
            # Adiciona o arquivo ao ZIP
            file_name = os.path.basename(file_key)  # Nome do arquivo sem o caminho
            zip_file.writestr(file_name, file_data)
    
    # Reseta o cursor do buffer
    zip_buffer.seek(0)

    # Fazer o upload do arquivo ZIP para o S3
    zip_key = job_output_directory + "imagens.zip"
    s3_client.put_object(Bucket=bucket_name, Key=zip_key, Body=zip_buffer.getvalue())

    zipPath = f"s3://{bucket_name}/{zip_key}"
    print(f"Arquivo ZIP enviado para: {zipPath}")

    # Apaga todos os arquivos do diretório de output, exceto o zip
    if 'Contents' in response:
        for obj in response['Contents']:
            file_key = obj['Key']
            if file_key != zip_key:
                print(f"Excluindo arquivo: {file_key}")
                s3_client.delete_object(Bucket=bucket_name, Key=file_key)

    # Retorna
    return {
        'statusCode': 200,
        'body': json.dumps(f"Arquivo ZIP criado e enviado para {zipPath}")
    }
