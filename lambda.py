import json
import boto3
import os
import zipfile
from io import BytesIO

def lambda_handler(event, context):
    # Bucket name
    bucket_name = "main-357"
    
    # Pega o id do job
    job_id = event.get("detail", {}).get("jobId")
    print(f"job_id: {job_id}")

    # Pega o arquivo de input do job
    job_input_key = event.get("detail", {}).get("outputGroupDetails", [])[0].get("outputDetails", [])[0].get("outputFilePaths", [])[0]
    job_input_key = job_input_key.replace("s3://main-357/", "").replace("output/", "input/")
    print(f"job_input_key: {job_input_key}")

    # Pega o diretório de output do job
    # Ele vem no formato "s3://main-357/idUsuario/numeroVideo/output/arquivo.mp4"
    job_output = event.get("detail", {}).get("outputGroupDetails", [])[0].get("outputDetails", [])[0].get("outputFilePaths", [])[0]
    job_output = job_output.replace("s3://main-357/", "")
    job_output = job_output.split("output/")[0] + "output/"
    print(f"job_output: {job_output}")
    
    # Lista os arquivos no diretório de output do job
    s3_client = boto3.client('s3')
    list_key_images = s3_client.list_objects_v2(Bucket=bucket_name, Prefix=job_output)

    # Armazena os endereços dos arquivos que serão compactados em um array
    files_to_compress = []
    msg_no_images = 'Não foi encontrada nenhuma imagem no diretório de saída.'

    if 'Contents' in list_key_images:
        for obj in list_key_images['Contents']:
            file_key = obj['Key']
            if not file_key.endswith('.mp4'): # Ignora arquivos mp4
                files_to_compress.append(file_key)
    else:
        print(msg_no_images)

    # Se não houver arquivos no array, retorna
    if not files_to_compress:
        return {
            'statusCode': 200,
            'body': json.dumps(msg_no_images)
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

    # Faz o upload do arquivo ZIP para o S3
    zip_key = job_output + "imagens.zip"
    s3_client.put_object(Bucket=bucket_name, Key=zip_key, Body=zip_buffer.getvalue())

    zipPath = f"s3://{bucket_name}/{zip_key}"
    print(f"Arquivo ZIP enviado para: {zipPath}")

    # Apaga o arquivo de input
    print(f"Excluindo arquivo: {job_input_key}")
    s3_client.delete_object(Bucket=bucket_name, Key=job_input_key)

    # Apaga todos os arquivos do diretório de output, exceto o zip
    if 'Contents' in list_key_images:
        for obj in list_key_images['Contents']:
            file_key = obj['Key']
            if file_key != zip_key:
                print(f"Excluindo arquivo: {file_key}")
                s3_client.delete_object(Bucket=bucket_name, Key=file_key)

    # Retorna
    return {
        'statusCode': 200,
        'body': json.dumps(f"Arquivo ZIP criado e enviado para {zipPath}")
    }
